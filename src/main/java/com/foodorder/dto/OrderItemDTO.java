package com.foodorder.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项DTO
 */
@Data
@ApiModel(value = "OrderItemDTO", description = "提交订单时的菜品项")
public class OrderItemDTO {

    @ApiModelProperty(value = "菜品ID", required = true)
    private Long foodId;

    @ApiModelProperty(value = "菜品名称", required = true)
    private String foodName;

    @ApiModelProperty(value = "菜品图片")
    private String foodImage;

    @ApiModelProperty(value = "单价", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;
}
