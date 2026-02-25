package com.foodorder.controller.admin;

import com.foodorder.common.Result;
import com.foodorder.entity.Food;
import com.foodorder.service.FoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 菜品管理Controller（管理员端）
 */
@Api(tags = "菜品管理")
@RestController
@RequestMapping("/food")
public class FoodAdminController {

    @Autowired
    private FoodService foodService;

    /**
     * 添加菜品
     */
    @ApiOperation(value = "添加菜品", notes = "管理员添加新菜品，传入name、categoryId、price、description、image字段")
    @PostMapping("/add")
    public Result<Void> add(
            @ApiParam(value = "菜品信息，包含name、categoryId、price、description、image", required = true)
            @RequestBody Map<String, Object> params) {
        Food food = new Food();
        food.setName((String) params.get("name"));
        food.setCategoryId(Long.valueOf(params.get("categoryId").toString()));
        food.setPrice(new BigDecimal(params.get("price").toString()));
        food.setDescription((String) params.get("description"));
        food.setImage((String) params.get("image"));

        foodService.addFood(food);
        return Result.success();
    }

    /**
     * 编辑菜品
     */
    @ApiOperation(value = "编辑菜品", notes = "管理员编辑菜品信息，传入id、name、categoryId、price、description、image字段")
    @PostMapping("/update")
    public Result<Void> update(
            @ApiParam(value = "菜品信息，包含id、name、categoryId、price、description、image", required = true)
            @RequestBody Map<String, Object> params) {
        Food food = new Food();
        food.setId(Long.valueOf(params.get("id").toString()));
        food.setName((String) params.get("name"));
        food.setCategoryId(Long.valueOf(params.get("categoryId").toString()));
        food.setPrice(new BigDecimal(params.get("price").toString()));
        food.setDescription((String) params.get("description"));
        food.setImage((String) params.get("image"));

        foodService.updateFood(food);
        return Result.success();
    }

    /**
     * 删除菜品
     */
    @ApiOperation(value = "删除菜品", notes = "管理员删除菜品（逻辑删除），传入菜品id")
    @PostMapping("/delete")
    public Result<Void> delete(
            @ApiParam(value = "删除参数，包含id", required = true)
            @RequestBody Map<String, Long> params) {
        Long id = params.get("id");
        foodService.deleteFood(id);
        return Result.success();
    }
}
