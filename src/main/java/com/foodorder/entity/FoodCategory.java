package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜品分类实体类
 */
@Data
@TableName("food_category")
@ApiModel(value = "FoodCategory", description = "菜品分类")
public class FoodCategory {

    @ApiModelProperty(value = "分类ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "排序号")
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否删除：0-否，1-是")
    @TableLogic
    private Integer isDeleted;
}
