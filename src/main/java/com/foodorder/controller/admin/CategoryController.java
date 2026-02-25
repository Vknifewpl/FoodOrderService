package com.foodorder.controller.admin;

import com.foodorder.common.Result;
import com.foodorder.entity.FoodCategory;
import com.foodorder.service.FoodCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类管理Controller（管理员端）
 */
@Api(tags = "分类管理")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private FoodCategoryService foodCategoryService;

    /**
     * 添加分类
     */
    @ApiOperation(value = "添加分类", notes = "管理员添加新的菜品分类，传入分类名称name")
    @PostMapping("/add")
    public Result<Void> add(
            @ApiParam(value = "分类参数，包含name", required = true)
            @RequestBody Map<String, String> params) {
        String name = params.get("name");
        if (name == null || name.trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        foodCategoryService.addCategory(name);
        return Result.success();
    }

    /**
     * 编辑分类
     */
    @ApiOperation(value = "编辑分类", notes = "管理员编辑菜品分类名称，传入分类id和新的名称name")
    @PostMapping("/update")
    public Result<Void> update(
            @ApiParam(value = "分类参数，包含id和name", required = true)
            @RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String name = (String) params.get("name");
        if (name == null || name.trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        foodCategoryService.updateCategory(id, name);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @ApiOperation(value = "删除分类", notes = "管理员删除菜品分类，传入分类id")
    @PostMapping("/delete")
    public Result<Void> delete(
            @ApiParam(value = "删除参数，包含id", required = true)
            @RequestBody Map<String, Long> params) {
        Long id = params.get("id");
        foodCategoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 获取所有分类
     */
    @ApiOperation(value = "获取所有分类", notes = "获取系统中全部菜品分类列表")
    @GetMapping("/list")
    public Result<List<FoodCategory>> list() {
        List<FoodCategory> categories = foodCategoryService.listCategories();
        return Result.success(categories);
    }
}
