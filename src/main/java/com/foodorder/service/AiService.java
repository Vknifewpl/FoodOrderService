package com.foodorder.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foodorder.entity.Food;
import com.foodorder.mapper.FoodMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * AI智能推荐服务（Token优化版）
 * 使用极简菜品上下文 + 内存缓存 + 限制输出长度，最大化降低Token开销
 */
@Service
public class AiService {

        private static final Logger log = LoggerFactory.getLogger(AiService.class);

        @Value("${ai.api-key}")
        private String apiKey;

        @Value("${ai.base-url}")
        private String baseUrl;

        @Value("${ai.model}")
        private String model;

        @Autowired
        private FoodMapper foodMapper;

        private final RestTemplate restTemplate = new RestTemplate();

        /** 菜品上下文缓存（极简格式），避免每次请求都重新构建 */
        private final AtomicReference<CachedMenu> menuCache = new AtomicReference<>();

        /**
         * 获取极简菜品上下文（带缓存，30秒过期）
         * 格式：ID:名称:分类:价格 每行一个，极致精简
         */
        private CachedMenu getMenuContext() {
                CachedMenu cached = menuCache.get();
                if (cached != null && System.currentTimeMillis() - cached.timestamp < 30_000) {
                        return cached;
                }
                List<Food> foods = foodMapper.selectAllWithCategory();
                if (foods == null || foods.isEmpty()) {
                        return null;
                }
                // 极简格式：每个菜品只用 ID:名称:分类:价格
                String context = foods.stream()
                                .map(f -> f.getId() + ":" + f.getName() + ":" + f.getCategoryName() + ":"
                                                + f.getPrice().intValue())
                                .collect(Collectors.joining("\n"));
                CachedMenu newCache = new CachedMenu(context, foods, System.currentTimeMillis());
                menuCache.set(newCache);
                return newCache;
        }

        /**
         * 搭配推荐（Token极简版）
         */
        public JSONObject getRecommendation(Long addedFoodId, List<Long> cartFoodIds) {
                if (cartFoodIds != null && cartFoodIds.size() >= 3) {
                        return null;
                }
                CachedMenu menu = getMenuContext();
                if (menu == null)
                        return null;

                // 获取已点菜品名称
                String addedName = menu.foods.stream()
                                .filter(f -> f.getId().equals(addedFoodId))
                                .map(Food::getName).findFirst().orElse("?");
                String cartNames = cartFoodIds.stream()
                                .map(id -> menu.foods.stream().filter(f -> f.getId().equals(id))
                                                .map(Food::getName).findFirst().orElse(""))
                                .filter(n -> !n.isEmpty()).collect(Collectors.joining(","));
                String excludeIds = cartFoodIds.stream().map(String::valueOf).collect(Collectors.joining(","));

                // 极简Prompt
                String sys = "菜单(ID:名称:分类:价格):\n" + menu.context +
                                "\n排除ID:" + excludeIds +
                                "\n推荐1个不同分类的搭配菜品,纯JSON:{\"recommendFoodId\":数字,\"reason\":\"15字内理由\"}";
                String user = "点了" + addedName + ",车里有:" + cartNames;

                return callQwenApi(sys, user, 60);
        }

        /**
         * AI聊天对话（支持多轮对话上下文）
         */
        public JSONObject chat(String userMessage, List<Long> cartFoodIds, List<Map<String, String>> chatHistory) {
                CachedMenu menu = getMenuContext();
                if (menu == null) {
                        JSONObject fb = new JSONObject();
                        fb.put("reply", "暂时无法获取菜品信息。");
                        return fb;
                }

                String cartInfo = "空";
                if (cartFoodIds != null && !cartFoodIds.isEmpty()) {
                        cartInfo = cartFoodIds.stream()
                                        .map(id -> menu.foods.stream().filter(f -> f.getId().equals(id))
                                                        .map(Food::getName).findFirst().orElse(""))
                                        .filter(n -> !n.isEmpty()).collect(Collectors.joining(","));
                }

                // 增强的System Prompt：增加过敏/禁忌处理、上下文理解约束
                String sys = "你是「智味助手」，一个专业的点餐推荐AI。你必须严格遵守以下规则：\n"
                                + "【核心规则】\n"
                                + "1. 认真阅读并牢记用户在对话中提到的所有过敏信息、不喜欢的食材、饮食禁忌\n"
                                + "2. 绝对禁止推荐含有用户声明过敏或不喜欢的食材的菜品，即使菜品名称中包含该食材也不行\n"
                                + "3. 当用户提到过敏或禁忌时，先表示理解，然后推荐安全的替代品\n"
                                + "4. 结合对话上下文理解用户意图，记住用户之前表达的口味偏好\n"
                                + "5. 禁止推荐购物车中已有的菜品或本次对话中已经推荐过的菜品，搭配推荐必须是不同的菜品\n"
                                + "【菜单】(ID:名称:分类:价格)\n" + menu.context
                                + "\n【购物车】" + cartInfo
                                + "\n【重要：回复格式】你的每一次回复都必须是且仅是一个JSON对象，不要有任何其他文字。"
                                + "格式：{\"reply\":\"你的回复内容,≤80字\",\"recommendFoodId\":菜品ID数字或null}";

                // 构建包含历史的消息列表
                JSONArray messages = buildMessagesWithHistory(sys, chatHistory, userMessage);

                JSONObject result = callQwenApiWithMessages(messages, 200);
                if (result == null) {
                        JSONObject fb = new JSONObject();
                        fb.put("reply", "网络开小差了，请重新输入～");
                        return fb;
                }
                return result;
        }

        /**
         * 构建包含对话历史的消息数组
         * 限制最近6轮对话（12条消息），避免历史过长稀释System Prompt
         */
        private JSONArray buildMessagesWithHistory(String systemPrompt, List<Map<String, String>> chatHistory,
                        String currentMessage) {
                JSONArray messages = new JSONArray();

                // 1. System消息
                JSONObject sysMsg = new JSONObject();
                sysMsg.put("role", "system");
                sysMsg.put("content", systemPrompt);
                messages.add(sysMsg);

                // 2. 对话历史（限制最近6轮 = 12条消息）
                if (chatHistory != null && !chatHistory.isEmpty()) {
                        int maxHistory = Math.min(chatHistory.size(), 20);
                        List<Map<String, String>> recent = chatHistory.subList(chatHistory.size() - maxHistory,
                                        chatHistory.size());
                        for (Map<String, String> msg : recent) {
                                JSONObject historyMsg = new JSONObject();
                                historyMsg.put("role", msg.get("role"));
                                historyMsg.put("content", msg.get("content"));
                                messages.add(historyMsg);
                        }
                }

                // 3. 当前用户消息（末尾追加格式提醒，防止AI忘记JSON格式）
                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", currentMessage + "\n(请用JSON回复)");
                messages.add(userMsg);

                return messages;
        }

        /**
         * 调用通义千问API（支持max_tokens控制输出长度）
         */
        private JSONObject callQwenApi(String systemPrompt, String userPrompt, int maxTokens) {
                try {
                        JSONObject body = new JSONObject();
                        body.put("model", model);
                        body.put("temperature", 0.7);
                        body.put("max_tokens", maxTokens);

                        JSONArray messages = new JSONArray();
                        JSONObject sysMsg = new JSONObject();
                        sysMsg.put("role", "system");
                        sysMsg.put("content", systemPrompt);
                        messages.add(sysMsg);
                        JSONObject userMsg = new JSONObject();
                        userMsg.put("role", "user");
                        userMsg.put("content", userPrompt);
                        messages.add(userMsg);
                        body.put("messages", messages);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("Authorization", "Bearer " + apiKey);

                        ResponseEntity<String> response = restTemplate.exchange(
                                        baseUrl, HttpMethod.POST, new HttpEntity<>(body.toJSONString(), headers),
                                        String.class);

                        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                                JSONArray choices = JSON.parseObject(response.getBody()).getJSONArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                        String content = choices.getJSONObject(0)
                                                        .getJSONObject("message").getString("content").trim()
                                                        .replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
                                        return parseAiResponse(content);
                                }
                        }
                } catch (Exception e) {
                        log.error("AI调用失败: {}", e.getMessage());
                }
                return null;
        }

        /**
         * 调用通义千问API（接收已构建好的messages数组，支持多轮对话）
         */
        private JSONObject callQwenApiWithMessages(JSONArray messages, int maxTokens) {
                try {
                        JSONObject body = new JSONObject();
                        body.put("model", model);
                        body.put("temperature", 0.7);
                        body.put("max_tokens", maxTokens);
                        body.put("messages", messages);
                        // 强制JSON输出模式（通义千问兼容OpenAI参数）
                        JSONObject responseFormat = new JSONObject();
                        responseFormat.put("type", "json_object");
                        body.put("response_format", responseFormat);

                        log.info("AI多轮对话请求: 消息数={}, maxTokens={}", messages.size(), maxTokens);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("Authorization", "Bearer " + apiKey);

                        ResponseEntity<String> response = restTemplate.exchange(
                                        baseUrl, HttpMethod.POST, new HttpEntity<>(body.toJSONString(), headers),
                                        String.class);

                        log.info("AI多轮对话响应状态: {}", response.getStatusCode());

                        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                                JSONArray choices = JSON.parseObject(response.getBody()).getJSONArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                        String content = choices.getJSONObject(0)
                                                        .getJSONObject("message").getString("content").trim()
                                                        .replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
                                        log.info("AI多轮对话原始回复: {}", content);
                                        return parseAiResponse(content);
                                }
                        }
                } catch (Exception e) {
                        log.error("AI多轮对话调用失败: {}", e.getMessage(), e);
                }
                return null;
        }

        /**
         * 解析AI回复内容，支持三层容错：
         * 1. 直接JSON解析
         * 2. 从文本中正则提取 {...} JSON片段
         * 3. 将纯文本包装为 {"reply": "文本"}
         */
        private JSONObject parseAiResponse(String content) {
                // 第一层：直接解析
                try {
                        return JSON.parseObject(content);
                } catch (Exception ignored) {
                }

                // 第二层：尝试从文本中提取 JSON 片段
                java.util.regex.Matcher matcher = java.util.regex.Pattern
                                .compile("\\{[^}]*\"reply\"[^}]*\\}")
                                .matcher(content);
                if (matcher.find()) {
                        try {
                                return JSON.parseObject(matcher.group());
                        } catch (Exception ignored) {
                        }
                }

                // 第三层：将纯文本包装为回复JSON
                log.warn("AI未返回JSON格式，将纯文本包装为回复: {}", content);
                JSONObject fallback = new JSONObject();
                fallback.put("reply", content);
                fallback.put("recommendFoodId", null);
                return fallback;
        }

        /** 菜品缓存内部类 */
        private static class CachedMenu {
                final String context;
                final List<Food> foods;
                final long timestamp;

                CachedMenu(String context, List<Food> foods, long timestamp) {
                        this.context = context;
                        this.foods = foods;
                        this.timestamp = timestamp;
                }
        }
}
