package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 菜品Mapper
 */
@Mapper
public interface FoodMapper extends BaseMapper<Food> {

    /**
     * 根据分类ID查询菜品列表
     */
    @Select("SELECT f.*, c.name as category_name FROM food f " +
            "LEFT JOIN food_category c ON f.category_id = c.id " +
            "WHERE f.category_id = #{categoryId} AND f.is_deleted = 0 " +
            "ORDER BY f.create_time DESC")
    List<Food> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据名称模糊搜索
     */
    @Select("SELECT f.*, c.name as category_name FROM food f " +
            "LEFT JOIN food_category c ON f.category_id = c.id " +
            "WHERE f.name LIKE CONCAT('%', #{keyword}, '%') AND f.is_deleted = 0 " +
            "ORDER BY f.order_count DESC")
    List<Food> searchByName(@Param("keyword") String keyword);

    /**
     * 获取热门菜品TOP10
     */
    @Select("SELECT f.*, c.name as category_name FROM food f " +
            "LEFT JOIN food_category c ON f.category_id = c.id " +
            "WHERE f.is_deleted = 0 " +
            "ORDER BY f.order_count DESC LIMIT 10")
    List<Food> selectHotTop10();

    /**
     * 获取好评排行榜
     */
    @Select("SELECT f.*, c.name as category_name FROM food f " +
            "LEFT JOIN food_category c ON f.category_id = c.id " +
            "WHERE f.is_deleted = 0 " +
            "ORDER BY f.praise_count DESC")
    List<Food> selectPraiseRank();

    /**
     * 获取所有菜品（带分类名称）
     */
    @Select("SELECT f.*, c.name as category_name FROM food f " +
            "LEFT JOIN food_category c ON f.category_id = c.id " +
            "WHERE f.is_deleted = 0 " +
            "ORDER BY f.create_time DESC")
    List<Food> selectAllWithCategory();

    /**
     * 根据ID获取菜品（带分类名称）
     */
    @Select("SELECT f.*, c.name as category_name FROM food f " +
            "LEFT JOIN food_category c ON f.category_id = c.id " +
            "WHERE f.id = #{id} AND f.is_deleted = 0")
    Food selectByIdWithCategory(@Param("id") Long id);

    /**
     * 增加点餐次数
     */
    @Update("UPDATE food SET order_count = order_count + #{count} WHERE id = #{id}")
    int incrementOrderCount(@Param("id") Long id, @Param("count") Integer count);

    /**
     * 增加好评数量
     */
    @Update("UPDATE food SET praise_count = praise_count + 1 WHERE id = #{id}")
    int incrementPraiseCount(@Param("id") Long id);

    /**
     * 根据分类ID统计菜品数量
     */
    @Select("SELECT COUNT(*) FROM food WHERE category_id = #{categoryId} AND is_deleted = 0")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
