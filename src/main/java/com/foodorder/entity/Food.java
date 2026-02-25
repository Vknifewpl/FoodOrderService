package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 */
@Data
@TableName("food")
@ApiModel(value = "Food", description = "菜品信息")
public class Food {

    @ApiModelProperty(value = "菜品ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "菜品名称")
    private String name;

    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "菜品描述")
    private String description;

    @ApiModelProperty(value = "图片路径")
    private String image;

    @ApiModelProperty(value = "点餐次数")
    private Integer orderCount;

    @ApiModelProperty(value = "好评数量")
    private Integer praiseCount;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否删除：0-否，1-是")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "分类名称（非数据库字段）")
    @TableField(exist = false)
    private String categoryName;
}
