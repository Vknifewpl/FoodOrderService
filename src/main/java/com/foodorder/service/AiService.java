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
         * AI聊天对话（Token极简版）
         */
        public JSONObject chat(String userMessage, List<Long> cartFoodIds) {
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

                String sys = "你是智味助手,帮用户点餐。菜单(ID:名称:分类:价格):\n" + menu.context +
                                "\n购物车:" + cartInfo +
                                "\n回复简洁≤50字,推荐时带ID。纯JSON:{\"reply\":\"回复\",\"recommendFoodId\":数字或null}";

                JSONObject result = callQwenApi(sys, userMessage, 120);
                if (result == null) {
                        JSONObject fb = new JSONObject();
                        fb.put("reply", "没听清，再说一次吧～");
                        return fb;
                }
                return result;
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
                                        return JSON.parseObject(content);
                                }
                        }
                } catch (Exception e) {
                        log.error("AI调用失败: {}", e.getMessage());
                }
                return null;
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
