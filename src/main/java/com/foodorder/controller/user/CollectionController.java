package com.foodorder.controller.user;

import com.foodorder.common.Result;
import com.foodorder.entity.Food;
import com.foodorder.service.CollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏Controller（用户端）
 */
@Api(tags = "用户收藏")
@RestController
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    /**
     * 添加收藏（userId从Token拦截器中获取）
     */
    @ApiOperation(value = "添加收藏", notes = "将指定菜品添加到用户的收藏列表中，传入foodId")
    @PostMapping("/add")
    public Result<Void> add(
            @RequestAttribute Long userId,
            @ApiParam(value = "收藏参数，包含foodId", required = true) @RequestBody Map<String, Long> params) {
        Long foodId = params.get("foodId");
        collectionService.addCollection(userId, foodId);
        return Result.success();
    }

    /**
     * 取消收藏（userId从Token拦截器中获取）
     */
    @ApiOperation(value = "取消收藏", notes = "将指定菜品从用户的收藏列表中移除，传入foodId")
    @PostMapping("/delete")
    public Result<Void> delete(
            @RequestAttribute Long userId,
            @ApiParam(value = "取消收藏参数，包含foodId", required = true) @RequestBody Map<String, Long> params) {
        Long foodId = params.get("foodId");
        collectionService.deleteCollection(userId, foodId);
        return Result.success();
    }

    /**
     * 获取收藏列表（userId从Token拦截器中获取）
     */
    @ApiOperation(value = "获取收藏列表", notes = "获取当前用户收藏的所有菜品")
    @GetMapping("/list")
    public Result<List<Food>> list(@RequestAttribute Long userId) {
        List<Food> foods = collectionService.listCollections(userId);
        return Result.success(foods);
    }

    /**
     * 检查是否已收藏
     */
    @ApiOperation(value = "检查收藏状态", notes = "检查当前用户是否已收藏指定菜品")
    @GetMapping("/check")
    public Result<Boolean> check(
            @RequestAttribute Long userId,
            @ApiParam(value = "菜品ID", required = true) @RequestParam Long foodId) {
        boolean isCollected = collectionService.isCollected(userId, foodId);
        return Result.success(isCollected);
    }
}
