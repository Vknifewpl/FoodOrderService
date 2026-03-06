package com.foodorder.service.impl;

import com.foodorder.entity.Comment;
import com.foodorder.entity.Order;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.CommentMapper;
import com.foodorder.mapper.OrderMapper;
import com.foodorder.service.CommentService;
import com.foodorder.service.FoodService;
import com.foodorder.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论Service实现
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private FoodService foodService;

    @Autowired
    private RecommendService recommendService;

    @Override
    @Transactional
    public void addComment(Long userId, Long foodId, Long orderId, String content, Integer rating) {
        // 验证订单是否存在且已支付
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() < 2) {
            throw new BusinessException("只能对已完成的订单进行评论");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评论自己的订单");
        }

        // 检查是否已评论
        Comment exist = commentMapper.selectByUserAndFoodAndOrder(userId, foodId, orderId);
        if (exist != null) {
            throw new BusinessException("该菜品已评论过");
        }

        // 添加评论
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setFoodId(foodId);
        comment.setOrderId(orderId);
        comment.setContent(content);
        comment.setRating(rating);
        commentMapper.insert(comment);

        // 如果评分>=4，增加好评数量
        if (rating >= 4) {
            foodService.incrementPraiseCount(foodId);
        }

        // 更新用户偏好
        recommendService.updateUserPreference(userId, foodId, "COMMENT", null, rating);

        // 刷新推荐缓存
        recommendService.refreshRecommendCache(userId);
    }

    @Override
    public List<Comment> listCommentsByFood(Long foodId) {
        return commentMapper.selectByFoodId(foodId);
    }

    @Override
    public List<Comment> listCommentsByUser(Long userId) {
        return commentMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void addCommentBatch(Long userId, Long orderId, List<java.util.Map<String, Object>> comments) {
        // 验证订单是否存在且已完成
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() < 2) {
            throw new BusinessException("只能对已完成的订单进行评论");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评论自己的订单");
        }

        // 批量添加评论
        for (java.util.Map<String, Object> item : comments) {
            Long foodId = Long.valueOf(item.get("foodId").toString());
            Integer rating = Integer.valueOf(item.get("rating").toString());
            String content = item.get("content") != null ? item.get("content").toString() : "";

            // 检查是否已评论
            Comment exist = commentMapper.selectByUserAndFoodAndOrder(userId, foodId, orderId);
            if (exist != null) {
                // 已评论过的菜品跳过
                continue;
            }

            // 添加评论
            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setFoodId(foodId);
            comment.setOrderId(orderId);
            comment.setContent(content);
            comment.setRating(rating);
            commentMapper.insert(comment);

            // 如果评分>=4，增加好评数量
            if (rating >= 4) {
                foodService.incrementPraiseCount(foodId);
            }

            // 更新用户偏好
            recommendService.updateUserPreference(userId, foodId, "COMMENT", null, rating);
        }

        // 刷新推荐缓存
        recommendService.refreshRecommendCache(userId);
    }

    @Override
    public List<Comment> listCommentsByOrder(Long orderId) {
        return commentMapper.selectByOrderId(orderId);
    }

    @Override
    public boolean isOrderCommented(Long orderId) {
        return commentMapper.countByOrderId(orderId) > 0;
    }
}
