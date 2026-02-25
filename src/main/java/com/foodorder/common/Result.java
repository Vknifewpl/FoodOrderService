package com.foodorder.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Result", description = "统一响应结果")
public class Result<T> {

    @ApiModelProperty(value = "状态码：200-成功，401-未登录，403-无权限，500-服务器错误")
    private Integer code;

    @ApiModelProperty(value = "提示信息")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未登录或Token已过期", null);
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限访问", null);
    }
}
