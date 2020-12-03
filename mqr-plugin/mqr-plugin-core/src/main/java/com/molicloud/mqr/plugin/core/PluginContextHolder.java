package com.molicloud.mqr.plugin.core;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.molicloud.mqr.plugin.core.define.PluginSettingDef;
import lombok.experimental.UtilityClass;

/**
 * 线程上下文中存储插件配置信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/3 2:1 下午
 */
@UtilityClass
public class PluginContextHolder {

    /**
     * 线程传递插件配置信息
     */
    private final ThreadLocal<PluginSettingDef> pluginHolder = new TransmittableThreadLocal<>();

    /**
     * 设置插件配置信息
     *
     * @param setting
     */
    public void setSetting(PluginSettingDef setting) {
        pluginHolder.set(setting);
    }

    /**
     * 获取插件配置信息
     *
     * @return
     */
    public PluginSettingDef getSetting() {
        return pluginHolder.get();
    }

    /**
     * 清理插件配置信息
     */
    public void clear() {
        pluginHolder.remove();
    }
}