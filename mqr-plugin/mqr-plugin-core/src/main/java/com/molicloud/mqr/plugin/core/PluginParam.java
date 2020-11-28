package com.molicloud.mqr.plugin.core;

import com.molicloud.mqr.plugin.core.define.AtDef;
import com.molicloud.mqr.plugin.core.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashSet;
import java.util.Set;

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
     * 机器人管理员列表
     */
    private String[] admins;

    /**
     * 机器人是否被At「@」
     */
    private boolean at = false;

    /**
     * 消息中的所有At信息
     */
    private Set<AtDef> ats = new LinkedHashSet<>();

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
