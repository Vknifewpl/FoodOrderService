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
     * 添加收藏
     */
    @ApiOperation(value = "添加收藏", notes = "将指定菜品添加到用户的收藏列表中，传入userId和foodId")
    @PostMapping("/add")
    public Result<Void> add(
            @ApiParam(value = "收藏参数，包含userId和foodId", required = true)
            @RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long foodId = params.get("foodId");
        collectionService.addCollection(userId, foodId);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @ApiOperation(value = "取消收藏", notes = "将指定菜品从用户的收藏列表中移除，传入userId和foodId")
    @PostMapping("/delete")
    public Result<Void> delete(
            @ApiParam(value = "取消收藏参数，包含userId和foodId", required = true)
            @RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long foodId = params.get("foodId");
        collectionService.deleteCollection(userId, foodId);
        return Result.success();
    }

    /**
     * 获取收藏列表
     */
    @ApiOperation(value = "获取收藏列表", notes = "根据用户ID获取该用户收藏的所有菜品，包含菜品详细信息")
    @GetMapping("/list")
    public Result<List<Food>> list(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        List<Food> foods = collectionService.listCollections(userId);
        return Result.success(foods);
    }
}
