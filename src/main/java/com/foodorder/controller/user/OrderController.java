package com.foodorder.controller.user;

import com.foodorder.common.Result;
import com.foodorder.dto.OrderItemDTO;
import com.foodorder.entity.Order;
import com.foodorder.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单Controller（用户端）
 */
@Api(tags = "用户订单")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     */
    @ApiOperation(value = "提交订单", notes = "传入orderItems数组提交订单，orderItems中每项包含foodId、foodName、foodImage、price、quantity字段")
    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(
            @RequestAttribute Long userId,
            @ApiParam(value = "订单参数，包含orderItems数组", required = true) @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("orderItems");

        List<OrderItemDTO> orderItems = new ArrayList<>();
        for (Map<String, Object> item : items) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setFoodId(Long.valueOf(item.get("foodId").toString()));
            dto.setFoodName((String) item.get("foodName"));
            dto.setFoodImage((String) item.get("foodImage"));
            dto.setPrice(new BigDecimal(item.get("price").toString()));
            dto.setQuantity(Integer.valueOf(item.get("quantity").toString()));
            orderItems.add(dto);
        }

        Map<String, Object> result = orderService.submitOrder(userId, orderItems);
        return Result.success(result);
    }

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "获取订单列表", notes = "获取当前用户订单列表，可选传入status筛选订单状态（0-待支付，1-已支付，2-已完成）")
    @GetMapping("/list")
    public Result<List<Order>> list(
            @RequestAttribute Long userId,
            @ApiParam(value = "订单状态：0-待支付，1-已支付，2-已完成") @RequestParam(required = false) Integer status) {
        List<Order> orders = orderService.listOrders(userId, status);
        return Result.success(orders);
    }

    /**
     * 获取订单详情
     */
    @ApiOperation(value = "获取订单详情", notes = "根据订单编号获取订单详细信息，包含订单明细列表")
    @GetMapping("/detail")
    public Result<Order> detail(
            @ApiParam(value = "订单编号", required = true) @RequestParam String orderNo) {
        Order order = orderService.getOrderDetail(orderNo);
        return Result.success(order);
    }

    /**
     * 模拟支付
     */
    @ApiOperation(value = "模拟支付", notes = "传入orderNo模拟订单支付，将订单状态从待支付更新为已支付")
    @PostMapping("/pay")
    public Result<Void> pay(
            @ApiParam(value = "支付参数，包含orderNo", required = true) @RequestBody Map<String, String> params) {
        String orderNo = params.get("orderNo");
        orderService.payOrder(orderNo);
        return Result.success();
    }
}
