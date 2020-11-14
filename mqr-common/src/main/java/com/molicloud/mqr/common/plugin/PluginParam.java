package com.molicloud.mqr.common.plugin;

import com.molicloud.mqr.common.plugin.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.common.plugin.enums.RobotEventEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件通用入参
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/6 4:00 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginParam<T> {

    /**
     * 消息发送者
     */
    private String from;

    /**
     * 消息接收者
     */
    private String to;

    /**
     * 发送的消息体
     */
    private T data;

    /**
     * 插件执行的触发类型枚举
     */
    private ExecuteTriggerEnum executeTriggerEnum;

    /**
     * 机器人事件
     */
    private RobotEventEnum robotEventEnum;
}
