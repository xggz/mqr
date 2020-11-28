package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.plugin.core.RobotContextHolder;
import com.molicloud.mqr.framework.util.ActionUtil;
import com.molicloud.mqr.framework.util.MessageUtil;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
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
                    Group group = bot.getGroup(Long.parseLong(toId));
                    if (messageEvent.getMessage() != null) {
                        Message groupMessage = MessageUtil.convertGroupMessage(messageEvent.getMessage(), group);
                        if (groupMessage != null) {
                            group.sendMessage(groupMessage);
                        }
                    }
                    if (messageEvent.getAction() != null) {
                        ActionUtil.handlerGroupAction(group, messageEvent.getAction());
                    }
                });
                break;
            case FRIEND_MSG:
                toIds.parallelStream().forEach(toId -> {
                    Friend friend = bot.getFriend(Long.parseLong(toId));
                    if (messageEvent.getMessage() != null) {
                        Message friendMessage = MessageUtil.convertFriendMessage(messageEvent.getMessage(), friend);
                        if (friendMessage != null) {
                            friend.sendMessage(friendMessage);
                        }
                    }
                    if (messageEvent.getAction() != null) {
                        ActionUtil.handlerFriendAction(friend, messageEvent.getAction());
                    }
                });
                break;
            default:
                break;
        }
    }
}