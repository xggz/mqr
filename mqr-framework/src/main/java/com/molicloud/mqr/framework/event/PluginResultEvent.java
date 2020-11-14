package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.common.plugin.PluginParam;
import com.molicloud.mqr.common.plugin.PluginResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件返回结果的异步处理事件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/12 10:10 上午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginResultEvent {

    /**
     * 插件钩子名
     */
    private String pluginHookName;

    /**
     * 插件通用入参
     */
    private PluginParam pluginParam;

    /**
     * 插件处理结果
     */
    private PluginResult pluginResult;
}