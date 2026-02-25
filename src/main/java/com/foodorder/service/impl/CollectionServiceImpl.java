package com.foodorder.service.impl;

import com.foodorder.entity.FoodCollection;
import com.foodorder.entity.Food;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.CollectionMapper;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.service.CollectionService;
import com.foodorder.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收藏Service实现
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private RecommendService recommendService;

    @Override
    @Transactional
    public void addCollection(Long userId, Long foodId) {
        // 检查菜品是否存在
        Food food = foodMapper.selectById(foodId);
        if (food == null) {
            throw new BusinessException("菜品不存在");
        }

        // 检查是否已收藏
        FoodCollection exist = collectionMapper.selectByUserIdAndFoodId(userId, foodId);
        if (exist != null) {
            throw new BusinessException("已收藏该菜品");
        }

        // 添加收藏
        FoodCollection collection = new FoodCollection();
        collection.setUserId(userId);
        collection.setFoodId(foodId);
        collectionMapper.insert(collection);

        // 更新用户偏好
        recommendService.updateUserPreference(userId, foodId, "COLLECT", null, null);

        // 刷新推荐缓存
        recommendService.refreshRecommendCache(userId);
    }

    @Override
    @Transactional
    public void deleteCollection(Long userId, Long foodId) {
        FoodCollection collection = collectionMapper.selectByUserIdAndFoodId(userId, foodId);
        if (collection == null) {
            throw new BusinessException("未收藏该菜品");
        }

        collectionMapper.deleteById(collection.getId());

        // 更新用户偏好（取消收藏）
        recommendService.updateUserPreference(userId, foodId, "UNCOLLECT", null, null);

        // 刷新推荐缓存
        recommendService.refreshRecommendCache(userId);
    }

    @Override
    public List<Food> listCollections(Long userId) {
        return collectionMapper.selectCollectionFoods(userId);
    }

    @Override
    public boolean isCollected(Long userId, Long foodId) {
        FoodCollection collection = collectionMapper.selectByUserIdAndFoodId(userId, foodId);
        return collection != null;
    }
}

