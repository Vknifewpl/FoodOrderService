package com.foodorder.service;

import com.foodorder.entity.Food;

import java.util.List;

/**
 * 推荐Service接口
 */
public interface RecommendService {

    /**
     * 获取推荐菜品列表
     */
    List<Food> getRecommendations(Long userId);

    /**
     * 更新用户偏好
     */
    void updateUserPreference(Long userId, Long foodId, String action, Integer orderCount, Integer rating);

    /**
     * 刷新用户推荐缓存
     */
    void refreshRecommendCache(Long userId);
}
