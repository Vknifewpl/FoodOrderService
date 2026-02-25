package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.FoodCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 菜品分类Mapper
 */
@Mapper
public interface FoodCategoryMapper extends BaseMapper<FoodCategory> {

    /**
     * 根据分类名称查询
     */
    @Select("SELECT * FROM food_category WHERE name = #{name} AND is_deleted = 0")
    FoodCategory selectByName(@Param("name") String name);
}
