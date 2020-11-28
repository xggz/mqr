package com.molicloud.mqr.plugin.core.event;

import com.molicloud.mqr.plugin.core.action.Action;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 消息事件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 1:52 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MessageEvent<T> {

    /**
     * 回复消息
     */
    private T message;

    /**
     * 插件动作（禁言/解除禁言/踢人等等）
     */
    private Action action;

    /**
     * 消息接收者数组
     */
    private List<String> toIds;

    /**
     * 机器人事件
     */
    private RobotEventEnum robotEventEnum;
}