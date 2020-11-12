package com.molicloud.mqr.framework.handler;

import com.molicloud.mqr.common.PluginParam;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.event.PluginResultEvent;
import com.molicloud.mqr.framework.util.PluginUtil;
import kotlin.coroutines.CoroutineContext;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 事件监听处理器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:01 下午
 */
@Slf4j
@Component
public class EventListeningHandler extends SimpleListenerHost {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 监听群消息
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus onGroupMessage(GroupMessageEvent event) {
        PluginParam pluginParam = new PluginParam();
        pluginParam.setFrom(String.valueOf(event.getSender().getId()));
        pluginParam.setTo(String.valueOf(event.getGroup().getId()));
        pluginParam.setData(event.getMessage().contentToString());
        pluginParam.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        PluginResult pluginResult = PluginUtil.executePlugin(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            eventPublisher.publishEvent(new PluginResultEvent(String.valueOf(event.getGroup().getId()), RobotEventEnum.GROUP_MSG, pluginResult));
        }
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听好友消息
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus onFriendsMessage(FriendMessageEvent event) {
        PluginParam pluginParam = new PluginParam();
        pluginParam.setFrom(String.valueOf(event.getFriend().getId()));
        pluginParam.setTo(String.valueOf(event.getBot().getId()));
        pluginParam.setData(event.getMessage().contentToString());
        pluginParam.setRobotEventEnum(RobotEventEnum.FRIEND_MSG);
        PluginResult pluginResult = PluginUtil.executePlugin(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            eventPublisher.publishEvent(new PluginResultEvent(String.valueOf(event.getFriend().getId()), RobotEventEnum.FRIEND_MSG, pluginResult));
        }
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    @Override
    public void handleException(CoroutineContext context, Throwable exception) {
        throw new RuntimeException("在事件处理中发生异常", exception);
    }
}