package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
@ApiModel(value = "Order", description = "订单信息")
public class Order {

    @ApiModelProperty(value = "订单ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "订单状态：0-待支付，1-已支付，2-已完成")
    private Integer status;

    @ApiModelProperty(value = "下单时间")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime completeTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否删除：0-否，1-是")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "订单明细列表（非数据库字段）")
    @TableField(exist = false)
    private List<OrderItem> orderItems;

    @ApiModelProperty(value = "用户名（非数据库字段）")
    @TableField(exist = false)
    private String username;

    @ApiModelProperty(value = "是否已评价（非数据库字段）")
    @TableField(exist = false)
    private Boolean isCommented;
}
