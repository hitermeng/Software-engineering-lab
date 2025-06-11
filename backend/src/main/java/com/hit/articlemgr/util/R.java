package com.hit.articlemgr.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 *
 * @author HIT
 */
@Data
@NoArgsConstructor
public class R<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> R<T> success() {
        return new R<>(200, "操作成功", null);
    }

    public static <T> R<T> success(T data) {
        return new R<>(200, "操作成功", data);
    }

    public static <T> R<T> success(String message, T data) {
        return new R<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> R<T> error() {
        return new R<>(500, "操作失败", null);
    }

    public static <T> R<T> error(String message) {
        return new R<>(500, message, null);
    }

    public static <T> R<T> error(Integer code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> error(Integer code, String message, T data) {
        return new R<>(code, message, data);
    }

    /**
     * 参数错误
     */
    public static <T> R<T> badRequest(String message) {
        return new R<>(400, message, null);
    }

    /**
     * 未授权
     */
    public static <T> R<T> unauthorized(String message) {
        return new R<>(401, message, null);
    }

    /**
     * 禁止访问
     */
    public static <T> R<T> forbidden(String message) {
        return new R<>(403, message, null);
    }

    /**
     * 资源不存在
     */
    public static <T> R<T> notFound(String message) {
        return new R<>(404, message, null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}