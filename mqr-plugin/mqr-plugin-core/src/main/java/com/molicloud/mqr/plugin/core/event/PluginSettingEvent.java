package com.molicloud.mqr.plugin.core.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件钩子配置更新事件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/3 2:25 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginSettingEvent {

    /**
     * 钩子名
     */
    private String name;

    /**
     * 配置值
     */
    private String value;
}