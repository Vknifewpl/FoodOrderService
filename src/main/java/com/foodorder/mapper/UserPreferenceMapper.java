package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户偏好Mapper
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {

    /**
     * 根据用户ID和菜品ID查询偏好
     */
    @Select("SELECT * FROM user_preference WHERE user_id = #{userId} AND food_id = #{foodId}")
    UserPreference selectByUserIdAndFoodId(@Param("userId") Long userId, @Param("foodId") Long foodId);

    /**
     * 获取用户所有偏好数据
     */
    @Select("SELECT * FROM user_preference WHERE user_id = #{userId}")
    List<UserPreference> selectByUserId(@Param("userId") Long userId);

    /**
     * 获取所有用户偏好数据（用于协同过滤）
     */
    @Select("SELECT * FROM user_preference")
    List<UserPreference> selectAll();

    /**
     * 获取所有有偏好数据的用户ID
     */
    @Select("SELECT DISTINCT user_id FROM user_preference")
    List<Long> selectAllUserIds();

    /**
     * 获取某菜品被哪些用户购买过
     */
    @Select("SELECT user_id FROM user_preference WHERE food_id = #{foodId} AND order_count > 0")
    List<Long> selectUserIdsByFoodId(@Param("foodId") Long foodId);
}
