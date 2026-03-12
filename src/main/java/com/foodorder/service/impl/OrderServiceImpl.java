package com.foodorder.service.impl;

import com.foodorder.dto.OrderItemDTO;
import com.foodorder.entity.Order;
import com.foodorder.entity.OrderItem;
import com.foodorder.exception.BusinessException;
import com.foodorder.mapper.OrderItemMapper;
import com.foodorder.mapper.OrderMapper;
import com.foodorder.service.CommentService;
import com.foodorder.service.FoodService;
import com.foodorder.service.OrderService;
import com.foodorder.service.RecommendService;
import com.foodorder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单Service实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private CommentService commentService;

    @Override
    @Transactional
    public Map<String, Object> submitOrder(Long userId, List<OrderItemDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException("订单项不能为空");
        }

        String orderNo = generateOrderNo();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO item : orderItems) {
            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setOrderTime(LocalDateTime.now());
        orderMapper.insert(order);

        for (OrderItemDTO item : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setFoodId(item.getFoodId());
            orderItem.setFoodName(item.getFoodName());
            orderItem.setFoodImage(item.getFoodImage());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            orderItemMapper.insert(orderItem);

            foodService.incrementOrderCount(item.getFoodId(), item.getQuantity());
            recommendService.updateUserPreference(userId, item.getFoodId(), "ORDER", item.getQuantity(), null);
        }

        userService.updateNewUserStatus(userId, 0);
        recommendService.refreshRecommendCache(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    @Transactional
    public void payOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，只有已支付订单才能完成");
        }

        order.setStatus(2);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public List<Order> listOrders(Long userId, Integer status) {
        List<Order> orders = orderMapper.selectByUserIdAndStatus(userId, status);
        for (Order order : orders) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            order.setOrderItems(items);
            if (order.getStatus() == 2) {
                order.setIsCommented(commentService.isOrderCommented(userId, order.getId()));
            } else {
                order.setIsCommented(false);
            }
        }
        return orders;
    }

    @Override
    public Order getOrderDetail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderNo(orderNo);
        order.setOrderItems(items);
        return order;
    }

    @Override
    public List<Order> listAllOrders(Integer status) {
        List<Order> orders = orderMapper.selectAllOrders(status);
        for (Order order : orders) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            order.setOrderItems(items);
        }
        return orders;
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderNo, Integer status) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (status == 2 && order.getStatus() != 1) {
            throw new BusinessException("只有已支付订单才能标记为已完成");
        }
        order.setStatus(status);
        if (status == 2) {
            order.setCompleteTime(LocalDateTime.now());
        }
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void applyRefund(Long userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不支持申请退款");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void approveRefund(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("该订单没有待审批的退款申请");
        }
        order.setStatus(4);
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD" + timestamp + uuid;
    }
}