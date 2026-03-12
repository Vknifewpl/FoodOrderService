package com.foodorder.service.impl;

import com.foodorder.entity.Comment;
import com.foodorder.entity.Order;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.CommentMapper;
import com.foodorder.mapper.OrderItemMapper;
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
    private OrderItemMapper orderItemMapper;

    @Autowired
    private FoodService foodService;

    @Autowired
    private RecommendService recommendService;

    @Override
    @Transactional
    public void addComment(Long userId, Long foodId, Long orderId, String content, Integer rating) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() < 2) {
            throw new BusinessException("只能对已完成的订单进行评价");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评价自己的订单");
        }
        if (orderItemMapper.countByOrderIdAndFoodId(orderId, foodId) <= 0) {
            throw new BusinessException("只能评价订单中的菜品");
        }

        Comment exist = commentMapper.selectByUserAndFoodAndOrder(userId, foodId, orderId);
        if (exist != null) {
            throw new BusinessException("该菜品已评价过");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setFoodId(foodId);
        comment.setOrderId(orderId);
        comment.setContent(content);
        comment.setRating(rating);
        commentMapper.insert(comment);

        if (rating >= 4) {
            foodService.incrementPraiseCount(foodId);
        }

        recommendService.updateUserPreference(userId, foodId, "COMMENT", null, rating);
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
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() < 2) {
            throw new BusinessException("只能对已完成的订单进行评价");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评价自己的订单");
        }

        for (java.util.Map<String, Object> item : comments) {
            Long foodId = Long.valueOf(item.get("foodId").toString());
            Integer rating = Integer.valueOf(item.get("rating").toString());
            String content = item.get("content") != null ? item.get("content").toString() : "";

            if (orderItemMapper.countByOrderIdAndFoodId(orderId, foodId) <= 0) {
                throw new BusinessException("评价菜品不在订单中，foodId=" + foodId);
            }

            Comment exist = commentMapper.selectByUserAndFoodAndOrder(userId, foodId, orderId);
            if (exist != null) {
                continue;
            }

            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setFoodId(foodId);
            comment.setOrderId(orderId);
            comment.setContent(content);
            comment.setRating(rating);
            commentMapper.insert(comment);

            if (rating >= 4) {
                foodService.incrementPraiseCount(foodId);
            }

            recommendService.updateUserPreference(userId, foodId, "COMMENT", null, rating);
        }

        recommendService.refreshRecommendCache(userId);
    }

    @Override
    public List<Comment> listCommentsByOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该订单评价");
        }
        return commentMapper.selectByOrderId(userId, orderId);
    }

    @Override
    public boolean isOrderCommented(Long userId, Long orderId) {
        int orderFoodCount = orderItemMapper.countDistinctFoodByOrderId(orderId);
        if (orderFoodCount <= 0) {
            return false;
        }
        int commentedFoodCount = commentMapper.countDistinctFoodByUserAndOrder(userId, orderId);
        return commentedFoodCount >= orderFoodCount;
    }
}