package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@TableName("collection")
@ApiModel(value = "FoodCollection", description = "用户收藏")
public class FoodCollection {

    @ApiModelProperty(value = "收藏ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "菜品ID")
    private Long foodId;

    @ApiModelProperty(value = "收藏时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "菜品信息（非数据库字段）")
    @TableField(exist = false)
    private Food food;
}
