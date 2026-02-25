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
}
