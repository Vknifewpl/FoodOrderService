package com.foodorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
@ApiModel(value = "User", description = "用户信息")
public class User {

    @ApiModelProperty(value = "用户ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码（加密存储）")
    private String password;

    @ApiModelProperty(value = "角色：0-普通用户，1-管理员")
    private Integer role;

    @ApiModelProperty(value = "登录Token")
    private String token;

    @ApiModelProperty(value = "是否新用户：1-是，0-否")
    private Integer isNewUser;

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
