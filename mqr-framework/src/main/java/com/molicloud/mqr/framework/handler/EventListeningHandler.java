package com.molicloud.mqr.framework.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.PluginParam;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.PluginRegistrar;
import com.molicloud.mqr.framework.common.PluginHook;
import com.molicloud.mqr.framework.util.PluginHookUtil;
import com.molicloud.mqr.framework.util.PluginUtil;
import kotlin.coroutines.CoroutineContext;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        pluginParam.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        PluginResult pluginResult = executePlugin(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            Object reply = pluginResult.getData();
            if (reply instanceof String) {
                event.getGroup().sendMessage(String.valueOf(pluginResult.getData()));
            }
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
        PluginResult pluginResult = executePlugin(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            Object reply = pluginResult.getData();
            if (reply instanceof String) {
                event.getFriend().sendMessage(String.valueOf(pluginResult.getData()));
            }
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
        // 处理持有的插件钩子
        PluginResult pluginResult = executeHoldPluginHook(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            return pluginResult;
        }

        // 处理监听所有消息的插件钩子
        List<PluginHook> listeningAllMessagePluginHookList = PluginRegistrar.getListeningAllMessagePluginHookList(pluginParam.getRobotEventEnum());
        if (CollUtil.isNotEmpty(listeningAllMessagePluginHookList)) {
            pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.LISTENING_ALL_MESSAGE);
            pluginResult = executeAllPluginHook(listeningAllMessagePluginHookList, pluginParam);
            if (pluginResult != null && pluginResult.getProcessed()) {
                return pluginResult;
            }
        }

        // 如果消息为字符串，则处理常规的插件钩子
        if (pluginParam.getData() instanceof String) {
            List<PluginHook> normalPluginHookList = PluginRegistrar.getNormalPluginHookList(pluginParam.getRobotEventEnum());
            if (CollUtil.isNotEmpty(normalPluginHookList)) {
                // 过滤关键字
                List<PluginHook> keywordPluginHookList = normalPluginHookList.stream()
                        .filter(pluginHook -> pluginHook.getKeywords().contains(String.valueOf(pluginParam.getData())))
                        .collect(Collectors.toList());
                if (CollUtil.isNotEmpty(keywordPluginHookList)) {
                    pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.KEYWORD);
                    pluginResult = executeAllPluginHook(keywordPluginHookList, pluginParam);
                    if (pluginResult != null && pluginResult.getProcessed()) {
                        return pluginResult;
                    }
                }
            }
        }

        // 处理默认的插件钩子
        List<PluginHook> defaultPluginHookList = PluginRegistrar.getDefaultPluginHookList(pluginParam.getRobotEventEnum());
        if (CollUtil.isNotEmpty(defaultPluginHookList)) {
            pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.DEFAULTED);
            pluginResult = executeAllPluginHook(defaultPluginHookList, pluginParam);
            if (pluginResult != null && pluginResult.getProcessed()) {
                return pluginResult;
            }
        }

        return pluginResult;
    }

    /**
     * 执行持有的插件钩子（如果有的话）
     *
     * @param pluginParam
     * @return
     */
    private PluginResult executeHoldPluginHook(PluginParam pluginParam) {
        // 插件钩子名
        String name = null;
        // 判断群消息和好友消息的人是否持有对应的插件钩子
        if (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum())
                && PluginHookUtil.groupMemberHasPluginHook(pluginParam.getTo(), pluginParam.getFrom())) {
            name = PluginHookUtil.getGroupMemberHoldPluginHookName(pluginParam.getTo(), pluginParam.getFrom());
        } else if (RobotEventEnum.FRIEND_MSG.equals(pluginParam.getRobotEventEnum())
                && PluginHookUtil.friendHasPluginHook(pluginParam.getFrom())) {
            name = PluginHookUtil.getFriendHoldPluginHookName(pluginParam.getFrom());
        }

        if (StrUtil.isNotBlank(name)) {
            // 通过持有的插件钩子来执行
            pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.HOLD);
            return PluginUtil.execute(name, pluginParam);
        }
        return null;
    }

    /**
     * 按照优先级执行所有插件
     *
     * @param pluginParam
     * @return
     */
    private PluginResult executeAllPluginHook(List<PluginHook> pluginHookList, PluginParam pluginParam) {
        for (PluginHook pluginHook : pluginHookList) {
            if (pluginHook.getRobotEvents().contains(pluginParam.getRobotEventEnum())) {
                PluginResult pluginResult = PluginUtil.execute(pluginHook, pluginParam);
                if (pluginResult != null && pluginResult.getProcessed()) {
                    return pluginResult;
                }
            }
        }
        return null;
    }
}
