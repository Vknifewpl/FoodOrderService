package com.foodorder.controller.super_admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foodorder.common.Result;
import com.foodorder.entity.Order;
import com.foodorder.mapper.OrderMapper;
import com.foodorder.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 超级管理端 - 订单管理 Controller
 */
@Api(tags = "超级管理-订单管理")
@RestController
@RequestMapping("/super/orders")
public class SuperOrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表（支持订单号搜索、状态筛选）
     */
    @ApiOperation(value = "查询订单列表")
    @GetMapping
    public Result<IPage<Order>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getIsDeleted, 0)
                .like(StringUtils.hasText(keyword), Order::getOrderNo, keyword)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);

        return Result.success(orderMapper.selectPage(new Page<>(page, size), wrapper));
    }

    /**
     * 修改订单状态
     */
    @ApiOperation(value = "修改订单状态")
    @PutMapping("/{orderNo}/status")
    public Result<Void> updateStatus(@PathVariable String orderNo, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        orderService.updateOrderStatus(orderNo, status);
        return Result.success();
    }
}
