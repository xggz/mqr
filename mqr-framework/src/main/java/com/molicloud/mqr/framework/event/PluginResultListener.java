package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.properties.RobotProperties;
import com.molicloud.mqr.framework.util.MessageUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 监听插件返回结果的处理事件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/12 10:42 上午
 */
@Component
public class PluginResultListener {

    @Autowired
    private RobotProperties robotProperties;

    @Async
    @EventListener(PluginResultEvent.class)
    public void handlerResult(PluginResultEvent pluginResultEvent) {
        // 获取机器人实例
        Bot bot = Bot.getInstance(robotProperties.getQq());
        // 事件触发者的ID
        String ownerId = pluginResultEvent.getOwnerId();
        // 机器人事件枚举
        RobotEventEnum robotEventEnum = pluginResultEvent.getRobotEventEnum();
        // 插件返回的结果
        Object pluginResultData = pluginResultEvent.getPluginResult().getData();
        // 判断是否为消息类型的事件
        if (robotEventEnum.isMessageEvent()) {
            switch (robotEventEnum) {
                case GROUP_MSG:
                    Group group = bot.getGroup(Long.parseLong(ownerId));
                    Message groupMessage = MessageUtil.convertGroupMessage(pluginResultData, group);
                    if (groupMessage != null) {
                        group.sendMessage(groupMessage);
                    }
                    break;
                case FRIEND_MSG:
                    Friend friend = bot.getFriend(Long.parseLong(ownerId));
                    Message friendMessage = MessageUtil.convertFriendMessage(pluginResultData, friend);
                    if (friendMessage != null) {
                        friend.sendMessage(friendMessage);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}