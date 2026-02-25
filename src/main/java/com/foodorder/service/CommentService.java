package com.foodorder.service;

import com.foodorder.entity.Comment;

import java.util.List;

/**
 * 评论Service接口
 */
public interface CommentService {

    /**
     * 添加评论
     */
    void addComment(Long userId, Long foodId, Long orderId, String content, Integer rating);

    /**
     * 根据菜品ID获取评论列表
     */
    List<Comment> listCommentsByFood(Long foodId);

    /**
     * 根据用户ID获取评论列表
     */
    List<Comment> listCommentsByUser(Long userId);
}
