package com.foodorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foodorder.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单编号查询订单
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} AND is_deleted = 0")
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID和状态查询订单列表
     */
    @Select("<script>" +
            "SELECT o.*, u.username FROM orders o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "WHERE o.user_id = #{userId} AND o.is_deleted = 0 " +
            "<if test='status != null'> AND o.status = #{status}</if> " +
            "ORDER BY o.order_time DESC" +
            "</script>")
    List<Order> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 管理员查询所有订单
     */
    @Select("<script>" +
            "SELECT o.*, u.username FROM orders o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "WHERE o.is_deleted = 0 " +
            "<if test='status != null'> AND o.status = #{status}</if> " +
            "ORDER BY o.order_time DESC" +
            "</script>")
    List<Order> selectAllOrders(@Param("status") Integer status);

    /**
     * 统计总订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE is_deleted = 0")
    int countTotalOrders();

    /**
     * 统计总销售额（已支付订单）
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status >= 1 AND is_deleted = 0")
    BigDecimal sumTotalSales();
}
