package com.foodorder.service;

import com.foodorder.entity.FoodCategory;

import java.util.List;

/**
 * 菜品分类Service接口
 */
public interface FoodCategoryService {

    /**
     * 添加分类
     */
    void addCategory(String name);

    /**
     * 更新分类
     */
    void updateCategory(Long id, String name);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 获取所有分类
     */
    List<FoodCategory> listCategories();

    /**
     * 根据ID获取分类
     */
    FoodCategory getCategoryById(Long id);
}
