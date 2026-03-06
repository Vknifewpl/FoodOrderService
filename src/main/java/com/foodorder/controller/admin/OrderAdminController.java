package com.foodorder.controller.admin;

import com.foodorder.common.Result;
import com.foodorder.entity.Order;
import com.foodorder.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单管理Controller（管理员端）
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取所有订单
     */
    @ApiOperation(value = "获取所有订单", notes = "管理员获取所有订单列表，可选传入status筛选（0-待支付，1-已支付，2-已完成）")
    @GetMapping("/list")
    public Result<List<Order>> list(
            @ApiParam(value = "订单状态：0-待支付，1-已支付，2-已完成") @RequestParam(required = false) Integer status) {
        List<Order> orders = orderService.listAllOrders(status);
        return Result.success(orders);
    }

    /**
     * 更新订单状态
     */
    @ApiOperation(value = "更新订单状态", notes = "管理员更新订单状态，传入orderNo和目标status（0-待支付，1-已支付，2-已完成，3-退款申请中，4-已退款）")
    @PostMapping("/update")
    public Result<Void> update(
            @ApiParam(value = "更新参数，包含orderNo和status", required = true) @RequestBody Map<String, Object> params) {
        String orderNo = (String) params.get("orderNo");
        Integer status = Integer.valueOf(params.get("status").toString());
        orderService.updateOrderStatus(orderNo, status);
        return Result.success();
    }

    /**
     * 管理员同意退款
     */
    @ApiOperation(value = "同意退款", notes = "管理员审批退款申请，将订单状态从 3 改为 4（已退款）")
    @PostMapping("/approve-refund")
    public Result<Void> approveRefund(
            @ApiParam(value = "审批参数，包含orderNo", required = true) @RequestBody Map<String, String> params) {
        String orderNo = params.get("orderNo");
        orderService.approveRefund(orderNo);
        return Result.success();
    }
}
