package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户偏好实体类（协同过滤算法）
 */
@Data
@TableName("user_preference")
@ApiModel(value = "UserPreference", description = "用户偏好（协同过滤算法数据）")
public class UserPreference {

    @ApiModelProperty(value = "偏好ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "菜品ID")
    private Long foodId;

    @ApiModelProperty(value = "偏好得分")
    private BigDecimal preferenceScore;

    @ApiModelProperty(value = "点餐次数")
    private Integer orderCount;

    @ApiModelProperty(value = "是否收藏：0-否，1-是")
    private Integer isCollected;

    @ApiModelProperty(value = "评论评分")
    private Integer commentRating;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
