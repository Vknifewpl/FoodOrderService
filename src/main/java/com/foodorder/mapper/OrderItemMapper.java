package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单明细Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID查询明细
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单编号查询明细
     */
    @Select("SELECT * FROM order_item WHERE order_no = #{orderNo}")
    List<OrderItem> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 检查菜品是否属于订单
     */
    @Select("SELECT COUNT(*) FROM order_item WHERE order_id = #{orderId} AND food_id = #{foodId}")
    int countByOrderIdAndFoodId(@Param("orderId") Long orderId, @Param("foodId") Long foodId);

    /**
     * 统计订单中的不同菜品数
     */
    @Select("SELECT COUNT(DISTINCT food_id) FROM order_item WHERE order_id = #{orderId}")
    int countDistinctFoodByOrderId(@Param("orderId") Long orderId);
}
