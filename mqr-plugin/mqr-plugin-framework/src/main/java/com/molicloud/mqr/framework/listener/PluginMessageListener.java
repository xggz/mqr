package com.molicloud.mqr.framework.listener;

import com.molicloud.mqr.plugin.core.RobotContextHolder;
import com.molicloud.mqr.framework.util.ActionUtil;
import com.molicloud.mqr.framework.util.MessageUtil;
import com.molicloud.mqr.plugin.core.action.Action;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 监听插件主动推送的消息事件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 2:00 下午
 */
@Component
public class PluginMessageListener {

    @Async
    @EventListener(MessageEvent.class)
    public void handlerResult(MessageEvent messageEvent) {
        // 获取机器人实例
        Bot bot = Bot.getInstance(Long.parseLong(RobotContextHolder.getRobot().getQq()));
        // 机器人事件枚举
        RobotEventEnum robotEventEnum = messageEvent.getRobotEventEnum();
        // 消息接收者列表
        List<String> toIds = messageEvent.getToIds();
        switch (robotEventEnum) {
            case GROUP_MSG:
                toIds.parallelStream().forEach(toId -> {
                    handlerGroupMessage(bot.getGroup(Long.parseLong(toId)), messageEvent.getMessage(), messageEvent.getAction());
                });
                break;
            case FRIEND_MSG:
                toIds.parallelStream().forEach(toId -> {
                    handlerFriendMessage(bot.getFriend(Long.parseLong(toId)), messageEvent.getMessage(), messageEvent.getAction());
                });
                break;
            case TEMP_MSG:
                Group group = bot.getGroup(Long.parseLong(messageEvent.getHandlerId()));
                toIds.parallelStream().forEach(toId -> {
                    handlerTempMessage(group.get(Long.parseLong(toId)), messageEvent.getMessage(), messageEvent.getAction());
                });
                break;
            default:
                break;
        }
    }

    /**
     * 处理群消息
     *
     * @param group
     * @param message
     * @param action
     */
    private void handlerGroupMessage(Group group, Object message, Action action) {
        if (message != null) {
            Message groupMessage = MessageUtil.convertGroupMessage(message, group);
            if (groupMessage != null) {
                group.sendMessage(groupMessage);
            }
        }
        if (action != null) {
            ActionUtil.handlerGroupAction(group, action);
        }
    }

    /**
     * 处理好友消息
     *
     * @param friend
     * @param message
     * @param action
     */
    private void handlerFriendMessage(Friend friend, Object message, Action action) {
        if (message != null) {
            Message friendMessage = MessageUtil.convertPersonalMessage(message, friend);
            if (friendMessage != null) {
                friend.sendMessage(friendMessage);
            }
        }
        if (action != null) {
            ActionUtil.handlerFriendAction(friend, action);
        }
    }

    /**
     * 处理临时消息
     *
     * @param member
     * @param message
     * @param action
     */
    private void handlerTempMessage(Member member, Object message, Action action) {
        if (message != null) {
            Message tempMessage = MessageUtil.convertPersonalMessage(message, member);
            if (tempMessage != null) {
                member.sendMessage(tempMessage);
            }
        }
        if (action != null) {
            ActionUtil.handlerMemberAction(member, action);
        }
    }
}