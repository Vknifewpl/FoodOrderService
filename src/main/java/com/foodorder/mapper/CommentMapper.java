package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据菜品ID查询评论列表（带用户名）
     */
    @Select("SELECT c.*, u.username FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.food_id = #{foodId} AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectByFoodId(@Param("foodId") Long foodId);

    /**
     * 根据用户ID查询评论列表
     */
    @Select("SELECT c.*, u.username FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.user_id = #{userId} AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否已对某菜品评论
     */
    @Select("SELECT * FROM comment WHERE user_id = #{userId} AND food_id = #{foodId} AND order_id = #{orderId} AND is_deleted = 0")
    Comment selectByUserAndFoodAndOrder(@Param("userId") Long userId, @Param("foodId") Long foodId, @Param("orderId") Long orderId);

    /**
     * 根据订单ID查询评论列表（带菜品名称）
     */
    @Select("SELECT c.*, f.name as food_name, f.image as food_image FROM comment c " +
            "LEFT JOIN food f ON c.food_id = f.id " +
            "LEFT JOIN order_item oi ON c.order_id = oi.order_id AND c.food_id = oi.food_id " +
            "WHERE c.order_id = #{orderId} AND c.user_id = #{userId} AND c.is_deleted = 0 AND oi.id IS NOT NULL " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectByOrderId(@Param("userId") Long userId, @Param("orderId") Long orderId);

    /**
     * 统计订单的评论数量
     */
    @Select("SELECT COUNT(*) FROM comment WHERE order_id = #{orderId} AND is_deleted = 0")
    int countByOrderId(@Param("orderId") Long orderId);

    /**
     * 统计用户在订单内已评价的不同菜品数
     */
    @Select("SELECT COUNT(DISTINCT c.food_id) FROM comment c " +
            "LEFT JOIN order_item oi ON c.order_id = oi.order_id AND c.food_id = oi.food_id " +
            "WHERE c.user_id = #{userId} AND c.order_id = #{orderId} AND c.is_deleted = 0 AND oi.id IS NOT NULL")
    int countDistinctFoodByUserAndOrder(@Param("userId") Long userId, @Param("orderId") Long orderId);
}
