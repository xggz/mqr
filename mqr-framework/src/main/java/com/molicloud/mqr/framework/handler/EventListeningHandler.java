package com.molicloud.mqr.framework.handler;

import com.molicloud.mqr.common.PluginParam;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.PluginRegistrar;
import com.molicloud.mqr.framework.common.PluginHook;
import kotlin.coroutines.CoroutineContext;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事件监听处理器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:01 下午
 */
@Slf4j
@Component
public class EventListeningHandler extends SimpleListenerHost {

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
        pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.DEFAULT);
        pluginParam.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        PluginResult pluginResult = executePlugin(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            event.getGroup().sendMessage(String.valueOf(pluginResult.getData()));
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
        pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.DEFAULT);
        pluginParam.setRobotEventEnum(RobotEventEnum.FRIEND_MSG);
        PluginResult pluginResult = executePlugin(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            event.getFriend().sendMessage(String.valueOf(pluginResult.getData()));
        }
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    @Override
    public void handleException(CoroutineContext context, Throwable exception) {
        throw new RuntimeException("在事件处理中发生异常", exception);
    }

    /**
     * 执行插件并返回结果
     *
     * @param pluginParam
     * @return
     */
    private PluginResult executePlugin(PluginParam pluginParam) {
        // todo 机器人好友或群成员可以主动持有插件钩子
        // 获取所有的插件钩子，按照优先级执行
        List<PluginHook> pluginHookList = PluginRegistrar.getAllPluginHookList();
        for (PluginHook pluginHook : pluginHookList) {
            if (pluginHook.getRobotEvents().contains(pluginParam.getRobotEventEnum())) {
                try {
                    PluginResult pluginResult = pluginHook.getPHookMethod().execute(pluginParam);
                    if (pluginResult.getProcessed()) {
                        return pluginResult;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return null;
    }
}
