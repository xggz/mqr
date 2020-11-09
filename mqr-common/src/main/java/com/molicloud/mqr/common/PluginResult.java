package com.molicloud.mqr.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 插件通用返回结果
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/6 4:01 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否已处理
     */
    private Boolean processed = Boolean.FALSE;

    /**
     * 是否持有插件
     */
    private Boolean hold = Boolean.FALSE;

    /**
     * 处理结果
     */
    private T data;
}
