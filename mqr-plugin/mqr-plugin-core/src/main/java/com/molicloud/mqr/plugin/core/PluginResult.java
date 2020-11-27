package com.molicloud.mqr.plugin.core;

import com.molicloud.mqr.plugin.core.action.Action;
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
     * 插件动作（禁言/解除禁言/踢人等等）
     */
    private Action action;

    /**
     * 回复消息
     */
    private T message;
}
