package com.molicloud.mqr.common.rest;

/**
 * Rest Api 通用返回状态码
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 11:33 上午
 */
public enum ApiCode {

    /**
     * 请求成功状态码
     */
    SUCCESS("A0000", "请求成功"),

    /**
     * 请求失败状态码
     */
    FAILED("A0001", "请求失败：{0}"),

    /**
     * 请求未授权状态码
     */
    UNAUTHORIZED("A0101", "请求未授权"),

    /**
     * 系统异常
     */
    SYSERR("A9999", "系统异常");

    private final String code;

    private final String message;

    ApiCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
