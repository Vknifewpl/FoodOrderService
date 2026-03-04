package com.foodorder.controller.ai;

import com.alibaba.fastjson.JSONObject;
import com.foodorder.common.Result;
import com.foodorder.service.AiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI智能推荐Controller
 */
@Api(tags = "AI智能推荐")
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 获取AI搭配推荐
     * 前端在用户加入购物车后调用，返回推荐的搭配菜品
     */
    @ApiOperation(value = "获取AI搭配推荐", notes = "根据用户当前购物车内容推荐搭配菜品")
    @PostMapping("/recommend")
    public Result<JSONObject> recommend(@RequestBody Map<String, Object> params) {
        Long addedFoodId = Long.valueOf(params.get("currentAddedFoodId").toString());

        @SuppressWarnings("unchecked")
        List<Number> rawIds = (List<Number>) params.get("cartFoodIds");
        List<Long> cartFoodIds = rawIds.stream()
                .map(Number::longValue)
                .collect(java.util.stream.Collectors.toList());

        JSONObject result = aiService.getRecommendation(addedFoodId, cartFoodIds);
        if (result == null) {
            return Result.success(null);
        }
        return Result.success(result);
    }

    /**
     * AI聊天对话
     * 用户可以自由与AI对话，AI结合菜品信息进行推荐
     */
    @ApiOperation(value = "AI聊天对话", notes = "用户自由输入，AI回复推荐建议")
    @PostMapping("/chat")
    public Result<JSONObject> chat(@RequestBody Map<String, Object> params) {
        String message = (String) params.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }

        @SuppressWarnings("unchecked")
        List<Number> rawIds = params.get("cartFoodIds") != null
                ? (List<Number>) params.get("cartFoodIds")
                : java.util.Collections.emptyList();
        List<Long> cartFoodIds = rawIds.stream()
                .map(Number::longValue)
                .collect(java.util.stream.Collectors.toList());

        JSONObject result = aiService.chat(message, cartFoodIds);
        return Result.success(result);
    }
}
