package com.molicloud.mqr.plugin.core.define;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件配置信息定义
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/3 3:6 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginSettingDef {

    /**
     * 插件钩子名
     */
    private String name;

    /**
     * 配置值
     */
    private String settingValue;
}