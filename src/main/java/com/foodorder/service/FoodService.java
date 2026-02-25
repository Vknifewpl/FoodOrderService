package com.foodorder.service;

import com.foodorder.entity.Food;

import java.util.List;
import java.util.Map;

/**
 * 菜品Service接口
 */
public interface FoodService {

    /**
     * 添加菜品
     */
    void addFood(Food food);

    /**
     * 更新菜品
     */
    void updateFood(Food food);

    /**
     * 删除菜品
     */
    void deleteFood(Long id);

    /**
     * 获取所有菜品
     */
    List<Food> listFoods();

    /**
     * 根据ID获取菜品
     */
    Food getFoodById(Long id);

    /**
     * 根据分类查询菜品
     */
    List<Food> listByCategory(Long categoryId);

    /**
     * 搜索菜品
     */
    List<Food> searchByName(String keyword);

    /**
     * 获取热门菜品TOP10
     */
    List<Food> getHotRank();

    /**
     * 获取好评排行榜
     */
    List<Food> getPraiseRank();

    /**
     * 获取菜品详情（包含评论）
     */
    Map<String, Object> getFoodDetail(Long foodId);

    /**
     * 获取推荐菜品
     */
    List<Food> getRecommendFoods(Long userId);

    /**
     * 增加点餐次数
     */
    void incrementOrderCount(Long foodId, Integer count);

    /**
     * 增加好评数量
     */
    void incrementPraiseCount(Long foodId);
}
