package com.molicloud.mqr.common.rest;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Rest Api 公共返回格式
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 11:28 上午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Res<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    private T data;

    public Res() {}

    public Res(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Res(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Res(ApiCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public Res(ApiCode code, T data) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.data = data;
    }

    public static <T> Res<T> success() {
        return new Res<>(ApiCode.SUCCESS);
    }

    public static <T> Res<T> success(T data) {
        return new Res<>(ApiCode.SUCCESS, data);
    }

    /**
     * 用户请求错误状态码
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Res<T> failed(String message) {
        return new Res<>(ApiCode.FAILED.getCode(), MessageFormat.format(ApiCode.FAILED.getMessage(), message));
    }

    /**
     * 请求错误
     *
     * @param errorCode
     * @param <T>
     * @return
     */
    public static <T> Res<T> error(ApiCode errorCode) {
        return new Res<>(errorCode);
    }

    /**
     * 把对象转换成Json字符串
     *
     * @return
     */
    public String toJsonString() {
        return new JSONObject(this).toString();
    }
}
