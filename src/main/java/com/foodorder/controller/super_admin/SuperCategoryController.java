package com.foodorder.controller.super_admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodorder.common.Result;
import com.foodorder.entity.FoodCategory;
import com.foodorder.mapper.FoodCategoryMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 超级管理端 - 分类管理 Controller
 */
@Api(tags = "超级管理-分类管理")
@RestController
@RequestMapping("/super/categories")
public class SuperCategoryController {

    @Autowired
    private FoodCategoryMapper categoryMapper;

    /**
     * 查询所有分类（支持关键字搜索，分页）
     */
    @ApiOperation(value = "查询分类列表")
    @GetMapping
    public Result<IPage<FoodCategory>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<FoodCategory> wrapper = new LambdaQueryWrapper<FoodCategory>()
                .eq(FoodCategory::getIsDeleted, 0)
                .like(StringUtils.hasText(keyword), FoodCategory::getName, keyword)
                .orderByAsc(FoodCategory::getSort);

        return Result.success(categoryMapper.selectPage(new Page<>(page, size), wrapper));
    }

    /**
     * 新增分类
     */
    @ApiOperation(value = "新增分类")
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        FoodCategory category = new FoodCategory();
        category.setName((String) params.get("name"));
        category.setSort(params.get("sort") != null ? Integer.valueOf(params.get("sort").toString()) : 0);
        categoryMapper.insert(category);
        return Result.success();
    }

    /**
     * 编辑分类
     */
    @ApiOperation(value = "编辑分类")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        FoodCategory category = categoryMapper.selectById(id);
        if (category == null || category.getIsDeleted() == 1) {
            return Result.error("分类不存在");
        }
        if (StringUtils.hasText((String) params.get("name"))) {
            category.setName((String) params.get("name"));
        }
        if (params.get("sort") != null) {
            category.setSort(Integer.valueOf(params.get("sort").toString()));
        }
        categoryMapper.updateById(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @ApiOperation(value = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        FoodCategory category = categoryMapper.selectById(id);
        if (category == null || category.getIsDeleted() == 1) {
            return Result.error("分类不存在");
        }
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
