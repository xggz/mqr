package com.molicloud.mqr.plugin.core;

import com.molicloud.mqr.plugin.core.define.AtDef;
import com.molicloud.mqr.plugin.core.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

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
     * 消息发送者标识
     */
    private String from;

    /**
     * 消息发送者昵称
     */
    private String fromName;

    /**
     * 消息接收者标识
     */
    private String to;

    /**
     * 消息接收者昵称
     */
    private String toName;

    /**
     * 机器人是否被At「@」
     */
    private boolean at = false;

    /**
     * 插件被触发关键字/词
     */
    private String keyword = "";

    /**
     * 消息中的所有At信息
     */
    private List<AtDef> ats = new LinkedList<>();

    /**
     * 发送的消息体
     */
    private T data;

    /**
     * 持有的消息
     */
    private String holdMessage;

    /**
     * 插件执行的触发类型枚举
     */
    private ExecuteTriggerEnum executeTriggerEnum;

    /**
     * 机器人事件
     */
    private RobotEventEnum robotEventEnum;
}
