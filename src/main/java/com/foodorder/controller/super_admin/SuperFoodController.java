package com.foodorder.controller.super_admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodorder.common.Result;
import com.foodorder.entity.Food;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.service.FoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 超级管理端 - 菜品管理 Controller
 */
@Api(tags = "超级管理-菜品管理")
@RestController
@RequestMapping("/super/foods")
public class SuperFoodController {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private FoodService foodService;

    /**
     * 分页查询菜品列表（支持关键字搜索、分类筛选）
     */
    @ApiOperation(value = "查询菜品列表")
    @GetMapping
    public Result<IPage<Food>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<Food>()
                .eq(Food::getIsDeleted, 0)
                .like(StringUtils.hasText(keyword), Food::getName, keyword)
                .eq(categoryId != null, Food::getCategoryId, categoryId)
                .orderByDesc(Food::getCreateTime);

        IPage<Food> result = foodMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 新增菜品
     */
    @ApiOperation(value = "新增菜品")
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        foodService.addFood(buildFood(null, params));
        return Result.success();
    }

    /**
     * 编辑菜品
     */
    @ApiOperation(value = "编辑菜品")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        foodService.updateFood(buildFood(id, params));
        return Result.success();
    }

    /**
     * 删除菜品
     */
    @ApiOperation(value = "删除菜品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        foodService.deleteFood(id);
        return Result.success();
    }

    /** 从请求参数构建 Food 对象 */
    private Food buildFood(Long id, Map<String, Object> params) {
        Food food = new Food();
        if (id != null)
            food.setId(id);
        food.setName((String) params.get("name"));
        if (params.get("categoryId") != null) {
            food.setCategoryId(Long.valueOf(params.get("categoryId").toString()));
        }
        if (params.get("price") != null) {
            food.setPrice(new BigDecimal(params.get("price").toString()));
        }
        food.setDescription((String) params.get("description"));
        food.setImage((String) params.get("image"));
        return food;
    }
}
