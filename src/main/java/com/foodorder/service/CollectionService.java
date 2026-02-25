package com.foodorder.service;

import com.foodorder.entity.Food;

import java.util.List;

/**
 * 收藏Service接口
 */
public interface CollectionService {

    /**
     * 添加收藏
     */
    void addCollection(Long userId, Long foodId);

    /**
     * 取消收藏
     */
    void deleteCollection(Long userId, Long foodId);

    /**
     * 获取用户收藏列表
     */
    List<Food> listCollections(Long userId);

    /**
     * 检查是否已收藏
     */
    boolean isCollected(Long userId, Long foodId);
}
