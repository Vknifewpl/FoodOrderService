package com.foodorder.service.impl;

import com.foodorder.entity.Comment;
import com.foodorder.entity.Food;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.CommentMapper;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.service.FoodService;
import com.foodorder.service.RecommendService;
import com.foodorder.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜品Service实现
 */
@Service
public class FoodServiceImpl implements FoodService {

    private static final String HOT_RANK_KEY = "food:hot:rank";
    private static final long CACHE_EXPIRE = 3600; // 1小时

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RecommendService recommendService;

    @Override
    public void addFood(Food food) {
        food.setOrderCount(0);
        food.setPraiseCount(0);
        foodMapper.insert(food);
    }

    @Override
    public void updateFood(Food food) {
        Food exist = foodMapper.selectById(food.getId());
        if (exist == null) {
            throw new BusinessException("菜品不存在");
        }
        foodMapper.updateById(food);
    }

    @Override
    public void deleteFood(Long id) {
        Food exist = foodMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("菜品不存在");
        }
        foodMapper.deleteById(id);
    }

    @Override
    public List<Food> listFoods() {
        return foodMapper.selectAllWithCategory();
    }

    @Override
    public Food getFoodById(Long id) {
        return foodMapper.selectByIdWithCategory(id);
    }

    @Override
    public List<Food> listByCategory(Long categoryId) {
        return foodMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<Food> searchByName(String keyword) {
        return foodMapper.searchByName(keyword);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Food> getHotRank() {
        // 先从Redis获取
        Object cached = redisUtil.get(HOT_RANK_KEY);
        if (cached != null) {
            return (List<Food>) cached;
        }

        // 从数据库查询
        List<Food> hotFoods = foodMapper.selectHotTop10();

        // 缓存到Redis
        redisUtil.set(HOT_RANK_KEY, hotFoods, CACHE_EXPIRE);

        return hotFoods;
    }

    @Override
    public List<Food> getPraiseRank() {
        return foodMapper.selectPraiseRank();
    }

    @Override
    public List<Food> getPriceRank(String order) {
        return "asc".equalsIgnoreCase(order)
                ? foodMapper.selectByPriceAsc()
                : foodMapper.selectByPriceDesc();
    }

    @Override
    public List<Food> getSalesRank() {
        return foodMapper.selectBySalesRank();
    }

    @Override
    public Map<String, Object> getFoodDetail(Long foodId) {
        Food food = foodMapper.selectByIdWithCategory(foodId);
        if (food == null) {
            throw new BusinessException("菜品不存在");
        }

        List<Comment> comments = commentMapper.selectByFoodId(foodId);

        Map<String, Object> result = new HashMap<>();
        result.put("food", food);
        result.put("comments", comments);
        return result;
    }

    @Override
    public List<Food> getRecommendFoods(Long userId) {
        return recommendService.getRecommendations(userId);
    }

    @Override
    public void incrementOrderCount(Long foodId, Integer count) {
        foodMapper.incrementOrderCount(foodId, count);
        // 清除热门菜品缓存
        redisUtil.delete(HOT_RANK_KEY);
    }

    @Override
    public void incrementPraiseCount(Long foodId) {
        foodMapper.incrementPraiseCount(foodId);
    }
}
