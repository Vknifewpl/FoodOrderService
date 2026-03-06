package com.foodorder.service;

import com.foodorder.entity.Order;
import com.foodorder.dto.OrderItemDTO;

import java.util.List;
import java.util.Map;

/**
 * 订单Service接口
 */
public interface OrderService {

    /**
     * 提交订单
     */
    Map<String, Object> submitOrder(Long userId, List<OrderItemDTO> orderItems);

    /**
     * 模拟支付
     */
    void payOrder(String orderNo);

    /**
     * 完成订单
     */
    void completeOrder(String orderNo);

    /**
     * 获取用户订单列表
     */
    List<Order> listOrders(Long userId, Integer status);

    /**
     * 获取订单详情
     */
    Order getOrderDetail(String orderNo);

    /**
     * 管理员获取所有订单
     */
    List<Order> listAllOrders(Integer status);

    /**
     * 更新订单状态
     */
    void updateOrderStatus(String orderNo, Integer status);

    /**
     * 用户申请退款（状态 1/2 → 3）
     */
    void applyRefund(Long userId, String orderNo);

    /**
     * 管理员同意退款（状态 3 → 4）
     */
    void approveRefund(String orderNo);
}
