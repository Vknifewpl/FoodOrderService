package com.foodorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.foodorder.entity.FoodCategory;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.FoodCategoryMapper;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.service.FoodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜品分类Service实现
 */
@Service
public class FoodCategoryServiceImpl implements FoodCategoryService {

    @Autowired
    private FoodCategoryMapper foodCategoryMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public void addCategory(String name) {
        // 检查分类名是否已存在
        FoodCategory exist = foodCategoryMapper.selectByName(name);
        if (exist != null) {
            throw new BusinessException("分类名称已存在");
        }

        FoodCategory category = new FoodCategory();
        category.setName(name);
        category.setSort(0);
        foodCategoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Long id, String name) {
        FoodCategory category = foodCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类名是否已存在
        FoodCategory exist = foodCategoryMapper.selectByName(name);
        if (exist != null && !exist.getId().equals(id)) {
            throw new BusinessException("分类名称已存在");
        }

        category.setName(name);
        foodCategoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        FoodCategory category = foodCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有菜品关联
        int count = foodMapper.countByCategoryId(id);
        if (count > 0) {
            throw new BusinessException("该分类下还有菜品，无法删除");
        }

        foodCategoryMapper.deleteById(id);
    }

    @Override
    public List<FoodCategory> listCategories() {
        LambdaQueryWrapper<FoodCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(FoodCategory::getSort);
        return foodCategoryMapper.selectList(wrapper);
    }

    @Override
    public FoodCategory getCategoryById(Long id) {
        return foodCategoryMapper.selectById(id);
    }
}
