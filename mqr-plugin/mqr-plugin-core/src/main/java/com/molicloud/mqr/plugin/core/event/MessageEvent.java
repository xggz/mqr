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
     * 消息接收者数组（群或好友ID）
     */
    private List<String> toIds;

    /**
     * 处理ID（如临时消息的群ID，申请入群事件的群ID）
     */
    private String handlerId;

    /**
     * 机器人事件
     */
    private RobotEventEnum robotEventEnum;
}