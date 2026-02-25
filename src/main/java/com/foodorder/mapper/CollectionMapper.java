package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.FoodCollection;
import com.foodorder.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏Mapper
 */
@Mapper
public interface CollectionMapper extends BaseMapper<FoodCollection> {

    /**
     * 检查是否已收藏
     */
    @Select("SELECT * FROM collection WHERE user_id = #{userId} AND food_id = #{foodId}")
    FoodCollection selectByUserIdAndFoodId(@Param("userId") Long userId, @Param("foodId") Long foodId);

    /**
     * 获取用户收藏的菜品列表
     */
    @Select("SELECT f.*, fc.name as category_name FROM collection c " +
            "LEFT JOIN food f ON c.food_id = f.id " +
            "LEFT JOIN food_category fc ON f.category_id = fc.id " +
            "WHERE c.user_id = #{userId} AND f.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    List<Food> selectCollectionFoods(@Param("userId") Long userId);
}
