package com.foodorder.service.impl;

import com.foodorder.entity.Food;
import com.foodorder.entity.UserPreference;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.mapper.UserPreferenceMapper;
import com.foodorder.service.RecommendService;
import com.foodorder.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于用户的协同过滤推荐Service实现
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    private static final String RECOMMEND_CACHE_PREFIX = "recommend:user:";
    private static final long CACHE_EXPIRE = 3600; // 1小时
    private static final int TOP_SIMILAR_USERS = 10; // 取最相似的10个用户
    private static final int RECOMMEND_COUNT = 20; // 推荐菜品数量

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Food> getRecommendations(Long userId) {
        String cacheKey = RECOMMEND_CACHE_PREFIX + userId;

        // 读取缓存，兼容旧格式脏数据：若反序列化失败则删除 key 并重新计算
        try {
            Object cached = redisUtil.get(cacheKey);
            if (cached != null) {
                // 新格式：List<Long>（菜品ID列表）
                List<?> cachedIds = (List<?>) cached;
                List<Food> result = new ArrayList<>();
                for (Object id : cachedIds) {
                    Long foodId = ((Number) id).longValue();
                    Food food = foodMapper.selectByIdWithCategory(foodId);
                    if (food != null) {
                        result.add(food);
                    }
                }
                return result;
            }
        } catch (Exception e) {
            // 旧格式数据（如 ArrayList$SubList / List<Food>）无法反序列化，删除脏 key
            redisUtil.delete(cacheKey);
        }

        // 使用协同过滤算法生成推荐
        List<Food> recommendations = generateRecommendations(userId);

        // 如果推荐结果为空，返回好评排行榜
        if (recommendations.isEmpty()) {
            List<Food> praiseRank = foodMapper.selectPraiseRank();
            // 用 new ArrayList<> 包装避免 SubList 被缓存（SubList 无法反序列化）
            recommendations = praiseRank.size() > RECOMMEND_COUNT
                    ? new ArrayList<>(praiseRank.subList(0, RECOMMEND_COUNT))
                    : praiseRank;
        }

        // 只缓存ID列表，避免 Food 对象序列化/反序列化类型转换异常
        List<Long> foodIds = recommendations.stream()
                .map(Food::getId)
                .collect(Collectors.toList());
        redisUtil.set(cacheKey, foodIds, CACHE_EXPIRE);

        return recommendations;
    }

    /**
     * 基于用户的协同过滤算法
     */
    private List<Food> generateRecommendations(Long userId) {
        // 1. 获取目标用户的偏好数据
        List<UserPreference> userPrefs = userPreferenceMapper.selectByUserId(userId);
        if (userPrefs.isEmpty()) {
            return new ArrayList<>();
        }

        // 将用户偏好转换为Map
        Map<Long, BigDecimal> userPrefMap = new HashMap<>();
        for (UserPreference pref : userPrefs) {
            userPrefMap.put(pref.getFoodId(), pref.getPreferenceScore());
        }

        // 2. 获取所有用户ID
        List<Long> allUserIds = userPreferenceMapper.selectAllUserIds();
        allUserIds.remove(userId); // 排除自己

        if (allUserIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 计算与其他用户的相似度
        Map<Long, Double> similarityMap = new HashMap<>();
        for (Long otherUserId : allUserIds) {
            double similarity = calculateUserSimilarity(userId, otherUserId, userPrefMap);
            if (similarity > 0) {
                similarityMap.put(otherUserId, similarity);
            }
        }

        // 4. 获取最相似的N个用户
        List<Long> similarUsers = similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(TOP_SIMILAR_USERS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (similarUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 5. 获取相似用户喜欢但目标用户未购买的菜品
        Set<Long> userFoodIds = userPrefMap.keySet();
        Map<Long, Double> candidateFoods = new HashMap<>();

        for (Long similarUserId : similarUsers) {
            double similarity = similarityMap.get(similarUserId);
            List<UserPreference> otherPrefs = userPreferenceMapper.selectByUserId(similarUserId);

            for (UserPreference pref : otherPrefs) {
                // 排除用户已购买/收藏的菜品
                if (userFoodIds.contains(pref.getFoodId())) {
                    continue;
                }

                // 计算预测评分 = 相似度 * 偏好得分
                double predictScore = similarity * pref.getPreferenceScore().doubleValue();
                candidateFoods.merge(pref.getFoodId(), predictScore, Double::sum);
            }
        }

        // 6. 按预测评分排序，取TOP N
        List<Long> recommendFoodIds = candidateFoods.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(RECOMMEND_COUNT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendFoodIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 7. 查询菜品详情
        List<Food> recommendations = new ArrayList<>();
        for (Long foodId : recommendFoodIds) {
            Food food = foodMapper.selectByIdWithCategory(foodId);
            if (food != null) {
                recommendations.add(food);
            }
        }

        return recommendations;
    }

    /**
     * 计算两个用户的相似度（余弦相似度）
     */
    private double calculateUserSimilarity(Long userId1, Long userId2, Map<Long, BigDecimal> user1PrefMap) {
        List<UserPreference> user2Prefs = userPreferenceMapper.selectByUserId(userId2);
        if (user2Prefs.isEmpty()) {
            return 0;
        }

        Map<Long, BigDecimal> user2PrefMap = new HashMap<>();
        for (UserPreference pref : user2Prefs) {
            user2PrefMap.put(pref.getFoodId(), pref.getPreferenceScore());
        }

        // 找出共同评价的菜品
        Set<Long> commonFoods = new HashSet<>(user1PrefMap.keySet());
        commonFoods.retainAll(user2PrefMap.keySet());

        if (commonFoods.isEmpty()) {
            return 0;
        }

        // 计算余弦相似度
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (Long foodId : commonFoods) {
            double score1 = user1PrefMap.get(foodId).doubleValue();
            double score2 = user2PrefMap.get(foodId).doubleValue();
            dotProduct += score1 * score2;
        }

        for (BigDecimal score : user1PrefMap.values()) {
            norm1 += score.doubleValue() * score.doubleValue();
        }

        for (BigDecimal score : user2PrefMap.values()) {
            norm2 += score.doubleValue() * score.doubleValue();
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    @Override
    public void updateUserPreference(Long userId, Long foodId, String action, Integer orderCount, Integer rating) {
        // 查询是否已有偏好记录
        UserPreference preference = userPreferenceMapper.selectByUserIdAndFoodId(userId, foodId);

        if (preference == null) {
            preference = new UserPreference();
            preference.setUserId(userId);
            preference.setFoodId(foodId);
            preference.setPreferenceScore(BigDecimal.ZERO);
            preference.setOrderCount(0);
            preference.setIsCollected(0);
        }

        // 根据行为更新偏好
        switch (action) {
            case "ORDER":
                // 点餐：增加点餐次数
                preference.setOrderCount(preference.getOrderCount() + (orderCount != null ? orderCount : 1));
                break;
            case "COLLECT":
                // 收藏
                preference.setIsCollected(1);
                break;
            case "UNCOLLECT":
                // 取消收藏
                preference.setIsCollected(0);
                break;
            case "COMMENT":
                // 评论
                preference.setCommentRating(rating);
                break;
        }

        // 计算偏好得分: 点餐次数*3 + 收藏*2 + 评论评分
        BigDecimal score = new BigDecimal(preference.getOrderCount() * 3);
        score = score.add(new BigDecimal(preference.getIsCollected() * 2));
        if (preference.getCommentRating() != null) {
            score = score.add(new BigDecimal(preference.getCommentRating()));
        }
        preference.setPreferenceScore(score);

        // 保存或更新
        if (preference.getId() == null) {
            userPreferenceMapper.insert(preference);
        } else {
            userPreferenceMapper.updateById(preference);
        }
    }

    @Override
    public void refreshRecommendCache(Long userId) {
        String cacheKey = RECOMMEND_CACHE_PREFIX + userId;
        redisUtil.delete(cacheKey);
    }
}
