package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
@ApiModel(value = "Comment", description = "评论信息")
public class Comment {

    @ApiModelProperty(value = "评论ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "菜品ID")
    private Long foodId;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评分：1-5")
    private Integer rating;

    @ApiModelProperty(value = "评论时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "是否删除：0-否，1-是")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "用户名（非数据库字段）")
    @TableField(exist = false)
    private String username;

    @ApiModelProperty(value = "菜品名称（非数据库字段）")
    @TableField(exist = false)
    private String foodName;

    @ApiModelProperty(value = "菜品图片（非数据库字段）")
    @TableField(exist = false)
    private String foodImage;
}
