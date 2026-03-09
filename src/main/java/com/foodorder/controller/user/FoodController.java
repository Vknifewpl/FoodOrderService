package com.foodorder.controller.user;

import com.foodorder.common.Result;
import com.foodorder.entity.Food;
import com.foodorder.service.CollectionService;
import com.foodorder.service.FoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜品Controller（用户端）
 */
@Api(tags = "菜品浏览")
@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private CollectionService collectionService;

    /**
     * 获取推荐菜品
     */
    @ApiOperation(value = "获取推荐菜品", notes = "基于协同过滤算法，根据用户偏好推荐个性化菜品列表")
    @GetMapping("/recommend")
    public Result<List<Food>> recommend(@RequestAttribute Long userId) {
        List<Food> foods = foodService.getRecommendFoods(userId);
        return Result.success(foods);
    }

    /**
     * 获取好评排行榜
     */
    @ApiOperation(value = "获取好评排行榜", notes = "按好评数量降序排列，返回好评最多的菜品列表")
    @GetMapping("/praise-rank")
    public Result<List<Food>> praiseRank() {
        List<Food> foods = foodService.getPraiseRank();
        return Result.success(foods);
    }

    /**
     * 按价格排列
     */
    @ApiOperation(value = "按价格排列", notes = "order=asc价格从低到高，order=desc价格从高到低")
    @GetMapping("/price-rank")
    public Result<List<Food>> priceRank(
            @ApiParam(value = "排序方向: asc/desc", defaultValue = "asc") @RequestParam(defaultValue = "asc") String order) {
        List<Food> foods = foodService.getPriceRank(order);
        return Result.success(foods);
    }

    /**
     * 按销量排列
     */
    @ApiOperation(value = "按销量排列", notes = "按点餐次数降序排列")
    @GetMapping("/sales-rank")
    public Result<List<Food>> salesRank() {
        List<Food> foods = foodService.getSalesRank();
        return Result.success(foods);
    }

    /**
     * 获取热门菜品TOP10
     */
    @ApiOperation(value = "获取热门菜品TOP10", notes = "按点餐次数降序排列，返回点餐最多的前10道菜品")
    @GetMapping("/hot-rank")
    public Result<List<Food>> hotRank() {
        List<Food> foods = foodService.getHotRank();
        return Result.success(foods);
    }

    /**
     * 根据分类获取菜品
     */
    @ApiOperation(value = "按分类获取菜品", notes = "根据菜品分类ID获取该分类下的所有菜品")
    @GetMapping("/category")
    public Result<List<Food>> listByCategory(
            @ApiParam(value = "分类ID", required = true) @RequestParam Long categoryId) {
        List<Food> foods = foodService.listByCategory(categoryId);
        return Result.success(foods);
    }

    /**
     * 搜索菜品
     */
    @ApiOperation(value = "搜索菜品", notes = "根据关键字模糊搜索菜品名称")
    @GetMapping("/search")
    public Result<List<Food>> search(
            @ApiParam(value = "搜索关键字", required = true) @RequestParam String keyword) {
        List<Food> foods = foodService.searchByName(keyword);
        return Result.success(foods);
    }

    /**
     * 获取菜品详情
     */
    @ApiOperation(value = "获取菜品详情", notes = "根据菜品ID获取详细信息，如传入userId还会返回该用户是否已收藏该菜品")
    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(
            @ApiParam(value = "菜品ID", required = true) @RequestParam Long foodId,
            @ApiParam(value = "用户ID（可选，用于判断是否收藏）") @RequestParam(required = false) Long userId) {
        Map<String, Object> result = foodService.getFoodDetail(foodId);

        // 如果用户已登录，返回是否收藏
        if (userId != null) {
            boolean isCollected = collectionService.isCollected(userId, foodId);
            result.put("isCollected", isCollected);
        }

        return Result.success(result);
    }

    /**
     * 获取所有菜品
     */
    @ApiOperation(value = "获取所有菜品", notes = "获取系统中所有未删除的菜品列表")
    @GetMapping("/list")
    public Result<List<Food>> list() {
        List<Food> foods = foodService.listFoods();
        return Result.success(foods);
    }
}
