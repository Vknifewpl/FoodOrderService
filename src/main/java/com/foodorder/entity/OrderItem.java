package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 */
@Data
@TableName("order_item")
@ApiModel(value = "OrderItem", description = "订单明细")
public class OrderItem {

    @ApiModelProperty(value = "明细ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "菜品ID")
    private Long foodId;

    @ApiModelProperty(value = "菜品名称（冗余）")
    private String foodName;

    @ApiModelProperty(value = "菜品图片（冗余）")
    private String foodImage;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "小计金额")
    private BigDecimal subtotal;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
