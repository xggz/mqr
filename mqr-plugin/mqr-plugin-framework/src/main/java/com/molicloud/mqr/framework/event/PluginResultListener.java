package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.action.Action;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.framework.RobotContextHolder;
import com.molicloud.mqr.framework.util.ActionUtil;
import com.molicloud.mqr.framework.util.MessageUtil;
import com.molicloud.mqr.framework.util.PluginHookUtil;
import com.molicloud.mqr.service.RobotFriendService;
import com.molicloud.mqr.service.RobotGroupMemberService;
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
    private RobotFriendService robotFriendService;

    @Autowired
    private RobotGroupMemberService robotGroupMemberService;

    @Async
    @EventListener(PluginResultEvent.class)
    public void handlerResult(PluginResultEvent pluginResultEvent) {
        // 获取机器人实例
        Bot bot = Bot.getInstance(Long.parseLong(RobotContextHolder.getRobotInfo().getQq()));
        // 插件入参
        PluginParam pluginParam = pluginResultEvent.getPluginParam();
        // 机器人事件枚举
        RobotEventEnum robotEventEnum = pluginParam.getRobotEventEnum();
        // 插件返回的结果
        PluginResult pluginResult = pluginResultEvent.getPluginResult();
        // 回复的消息
        Object pluginResultMessage = pluginResult.getMessage();
        // 执行的动作
        Action action = pluginResult.getAction();

        // 判断是否为消息类型的事件
        if (robotEventEnum.isMessageEvent()) {
            switch (robotEventEnum) {
                case GROUP_MSG:
                    Group group = bot.getGroup(Long.parseLong(pluginParam.getTo()));
                    if (pluginResultMessage != null) {
                        Message groupMessage = MessageUtil.convertGroupMessage(pluginResultMessage, group);
                        if (groupMessage != null) {
                            group.sendMessage(groupMessage);
                        }
                    }
                    if (action != null) {
                        ActionUtil.handlerGroupAction(group, action);
                    }
                    // 持有/释放插件钩子
                    if (PluginHookUtil.actionGroupMemberPluginHook(pluginParam.getTo(), pluginParam.getFrom(), pluginResultEvent.getPluginHookName(), pluginResult.getHold())) {
                        robotGroupMemberService.handlerHoldAction(pluginParam.getTo(), pluginParam.getFrom(), pluginResult.getHold(), pluginResultEvent.getPluginHookName());
                    }
                    break;
                case FRIEND_MSG:
                    Friend friend = bot.getFriend(Long.parseLong(pluginParam.getFrom()));
                    if (pluginResultMessage != null) {
                        Message friendMessage = MessageUtil.convertFriendMessage(pluginResultMessage, friend);
                        if (friendMessage != null) {
                            friend.sendMessage(friendMessage);
                        }
                    }
                    if (action != null) {
                        ActionUtil.handlerFriendAction(friend, action);
                    }
                    // 持有/释放插件钩子
                    if (PluginHookUtil.actionFriendPluginHook(pluginParam.getFrom(), pluginResultEvent.getPluginHookName(), pluginResult.getHold())) {
                        robotFriendService.handlerHoldAction(pluginParam.getFrom(), pluginResult.getHold(), pluginResultEvent.getPluginHookName());
                    }
                    break;
                default:
                    break;
            }
        }
    }
}