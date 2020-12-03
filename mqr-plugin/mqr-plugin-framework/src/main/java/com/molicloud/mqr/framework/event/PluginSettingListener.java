package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.framework.PluginSettingInitialize;
import com.molicloud.mqr.plugin.core.event.PluginSettingEvent;
import com.molicloud.mqr.service.SysSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 监听插件配置的更新事件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/3 2:33 下午
 */
@Component
public class PluginSettingListener {

    @Autowired
    private SysSettingService sysSettingService;

    @Async
    @EventListener(PluginSettingEvent.class)
    public void handlerResult(PluginSettingEvent pluginSettingEvent) {
        SettingEnum settingEnum = SettingEnum.PLUGIN_HOOK.setName(pluginSettingEvent.getName());
        if (sysSettingService.savePluginSetting(settingEnum, pluginSettingEvent.getValue(), String.class)) {
            PluginSettingInitialize.putPluginSettingValue(pluginSettingEvent.getName(), pluginSettingEvent.getValue());
        }
    }
}