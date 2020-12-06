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

    /**
     * 处理消息并回复消息和处理动作
     *
     * @param message
     * @param action
     * @param <T>
     * @return
     */
    public static <T> PluginResult reply(T message, Action action) {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setAction(action);
        pluginResult.setMessage(message);
        return pluginResult;
    }

    /**
     * 处理消息并回复消息，不处理动作
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> PluginResult reply(T message) {
        return reply(message, null);
    }

    /**
     * 不处理消息
     *
     * @return
     */
    public static PluginResult noReply() {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(false);
        return pluginResult;
    }
}
