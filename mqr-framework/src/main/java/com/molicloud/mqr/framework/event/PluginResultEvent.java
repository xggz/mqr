package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.RobotEventEnum;
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
     * 事件触发者的ID
     */
    private String ownerId;

    /**
     * 机器人事件枚举
     */
    private RobotEventEnum robotEventEnum;

    /**
     * 插件处理结果
     */
    private PluginResult pluginResult;

    /**
     * 构造函数
     *
     * @param robotEventEnum
     * @param pluginResult
     */
    public PluginResultEvent(String ownerId, RobotEventEnum robotEventEnum, PluginResult pluginResult) {
        this.ownerId = ownerId;
        this.robotEventEnum = robotEventEnum;
        this.pluginResult = pluginResult;
    }
}