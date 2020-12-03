package com.molicloud.mqr.plugin.core;

import cn.hutool.json.JSONObject;
import com.molicloud.mqr.plugin.core.define.RobotDef;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import com.molicloud.mqr.plugin.core.event.PluginSettingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 插件执行器抽象接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 1:46 下午
 */
@Slf4j
@Component
public abstract class AbstractPluginExecutor implements PluginExecutor {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 插件主动推送消息事件
     *
     * @param messageEvent
     */
    protected void pushMessage(MessageEvent messageEvent) {
        eventPublisher.publishEvent(messageEvent);
    }

    @Override
    public PluginInfo getPluginInfo() {
        // 返回null，默认没有可执行的插件脚本，插件想定义脚本，可重写此方法
        return null;
    }

    /**
     * 获取插件钩子设置
     *
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T> T getHookSetting(Class<T> clazz) {
        try {
            T object = clazz.newInstance();
            if (object instanceof String
                    || object instanceof Integer
                    || object instanceof Long
                    || object instanceof Double) {
                return (T) PluginContextHolder.getSetting().getSettingValue();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return new JSONObject(PluginContextHolder.getSetting().getSettingValue()).toBean(clazz);
    }

    /**
     * 保存插件钩子设置
     *
     * @param object 配置值
     * @param <T>
     */
    protected <T> void saveHookSetting(T object) {
        try {
            String settingValue = "";
            if (object instanceof String
                    || object instanceof Integer
                    || object instanceof Long
                    || object instanceof Double) {
                settingValue = String.valueOf(object);
            } else {
                settingValue = new JSONObject(object).toString();
            }

            // 插件钩子配置信息
            PluginSettingEvent pluginSettingEvent = new PluginSettingEvent();
            pluginSettingEvent.setName(PluginContextHolder.getSetting().getName());
            pluginSettingEvent.setValue(settingValue);
            eventPublisher.publishEvent(pluginSettingEvent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取管理ID列表
     *
     * @return
     */
    protected String[] getAdmins() {
        return RobotContextHolder.getRobot().getAdmins();
    }

    /**
     * 获取群列表
     *
     * @return
     */
    protected List<RobotDef.Group> getGroupList() {
        return RobotContextHolder.getRobot().getGroupList();
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    protected List<RobotDef.Friend> getFriendList() {
        return RobotContextHolder.getRobot().getFriendList();
    }
}