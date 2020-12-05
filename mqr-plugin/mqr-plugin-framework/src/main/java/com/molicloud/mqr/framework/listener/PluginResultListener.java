package com.molicloud.mqr.framework.listener;

import com.molicloud.mqr.framework.listener.event.PluginResultEvent;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.RobotContextHolder;
import com.molicloud.mqr.framework.util.ActionUtil;
import com.molicloud.mqr.framework.util.MessageUtil;
import com.molicloud.mqr.framework.util.PluginHookUtil;
import com.molicloud.mqr.service.RobotFriendService;
import com.molicloud.mqr.service.RobotGroupMemberService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
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
        Bot bot = Bot.getInstance(Long.parseLong(RobotContextHolder.getRobot().getQq()));
        // 插件入参
        PluginParam pluginParam = pluginResultEvent.getPluginParam();
        // 机器人事件枚举
        RobotEventEnum robotEventEnum = pluginParam.getRobotEventEnum();
        // 插件返回的结果
        PluginResult pluginResult = pluginResultEvent.getPluginResult();
        // 判断是否为消息类型的事件
        if (robotEventEnum.isMessageEvent()) {
            switch (robotEventEnum) {
                case GROUP_MSG:
                    handlerGroupMessage(bot, pluginParam, pluginResult, pluginResultEvent.getPluginHookName());
                    break;
                case FRIEND_MSG:
                    handlerFriendMessage(bot, pluginParam, pluginResult, pluginResultEvent.getPluginHookName());
                    break;
                case TEMP_MSG:
                    handlerTempMessage(bot, pluginParam, pluginResult, pluginResultEvent.getPluginHookName());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理群消息
     *
     * @param bot
     * @param pluginParam
     * @param pluginResult
     * @param hookName
     */
    private void handlerGroupMessage(Bot bot, PluginParam pluginParam, PluginResult pluginResult, String hookName) {
        Group group = bot.getGroup(Long.parseLong(pluginParam.getTo()));
        if (pluginResult.getMessage() != null) {
            Message groupMessage = MessageUtil.convertGroupMessage(pluginResult.getMessage(), group);
            if (groupMessage != null) {
                group.sendMessage(groupMessage);
            }
        }
        if (pluginResult.getAction() != null) {
            ActionUtil.handlerGroupAction(group, pluginResult.getAction());
        }
        // 持有/释放插件钩子
        if (PluginHookUtil.actionGroupMemberPluginHook(pluginParam.getTo(), pluginParam.getFrom(), hookName, pluginResult.getHold())) {
            robotGroupMemberService.handlerHoldAction(pluginParam.getTo(), pluginParam.getFrom(), pluginResult.getHold(), hookName);
        }
    }

    /**
     * 处理好友消息
     *
     * @param bot
     * @param pluginParam
     * @param pluginResult
     * @param hookName
     */
    private void handlerFriendMessage(Bot bot, PluginParam pluginParam, PluginResult pluginResult, String hookName) {
        Friend friend = bot.getFriend(Long.parseLong(pluginParam.getFrom()));
        if (pluginResult.getMessage() != null) {
            Message friendMessage = MessageUtil.convertPersonalMessage(pluginResult.getMessage(), friend);
            if (friendMessage != null) {
                friend.sendMessage(friendMessage);
            }
        }
        if (pluginResult.getAction() != null) {
            ActionUtil.handlerFriendAction(friend, pluginResult.getAction());
        }
        // 持有/释放插件钩子
        if (PluginHookUtil.actionFriendPluginHook(pluginParam.getFrom(), hookName, pluginResult.getHold())) {
            robotFriendService.handlerHoldAction(pluginParam.getFrom(), pluginResult.getHold(), hookName);
        }
    }

    /**
     * 处理临时消息
     *
     * @param bot
     * @param pluginParam
     * @param pluginResult
     * @param hookName
     */
    private void handlerTempMessage(Bot bot, PluginParam pluginParam, PluginResult pluginResult, String hookName) {
        Member member = bot.getGroup(Long.parseLong(pluginParam.getTo())).get(Long.parseLong(pluginParam.getFrom()));
        if (pluginResult.getMessage() != null) {
            Message groupMessage = MessageUtil.convertPersonalMessage(pluginResult.getMessage(), member);
            if (groupMessage != null) {
                member.sendMessage(groupMessage);
            }
        }
        if (pluginResult.getAction() != null) {
            ActionUtil.handlerMemberAction(member, pluginResult.getAction());
        }
        // 持有/释放插件钩子
        if (PluginHookUtil.actionFriendPluginHook(pluginParam.getFrom(), hookName, pluginResult.getHold())) {
            robotFriendService.handlerHoldAction(pluginParam.getFrom(), pluginResult.getHold(), hookName);
        }
    }
}