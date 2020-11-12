package com.molicloud.mqr.framework.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.PluginParam;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.PluginHookRegistrar;
import com.molicloud.mqr.framework.common.PluginHook;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件工具类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/11 4:18 下午
 */
@Slf4j
@UtilityClass
public class PluginUtil {

    /**
     * 执行插件并返回结果
     *
     * @param pluginParam
     * @return
     */
    public PluginResult executePlugin(PluginParam pluginParam) {
        // 处理持有的插件钩子
        PluginResult pluginResult = executeHoldPluginHook(pluginParam);
        if (pluginResult != null && pluginResult.getProcessed()) {
            return pluginResult;
        }

        // 处理监听所有消息的插件钩子
        List<PluginHook> listeningAllMessagePluginHookList = PluginHookRegistrar.getListeningAllMessagePluginHookList(pluginParam.getRobotEventEnum());
        if (CollUtil.isNotEmpty(listeningAllMessagePluginHookList)) {
            pluginParam.setExecuteTriggerEnum(ExecuteTriggerEnum.LISTENING_ALL_MESSAGE);
            pluginResult = executeAllPluginHook(listeningAllMessagePluginHookList, pluginParam);
            if (pluginResult != null && pluginResult.getProcessed()) {
                return pluginResult;
            }
        }

        // 如果消息为字符串，则处理常规的插件钩子
        if (pluginParam.getData() instanceof String) {
            List<PluginHook> normalPluginHookList = PluginHookRegistrar.getNormalPluginHookList(pluginParam.getRobotEventEnum());
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
        List<PluginHook> defaultPluginHookList = PluginHookRegistrar.getDefaultPluginHookList(pluginParam.getRobotEventEnum());
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
            return executePluginHook(name, pluginParam);
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
                PluginResult pluginResult = executePluginHook(pluginHook, pluginParam);
                if (pluginResult != null && pluginResult.getProcessed()) {
                    return pluginResult;
                }
            }
        }
        return null;
    }

    /**
     * 插件执行
     *
     * @param pluginHook
     * @param pluginParam
     * @return
     */
    private PluginResult executePluginHook(PluginHook pluginHook, PluginParam pluginParam) {
        PluginResult pluginResult = null;
        try {
            pluginResult = pluginHook.getPHookMethod().execute(pluginParam);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (pluginResult != null && pluginResult.getProcessed()) {
            if (pluginResult.getHold()) {
                if (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum())) {
                    PluginHookUtil.holdGroupMemberPluginHook(pluginParam.getTo(), pluginParam.getFrom(), pluginHook.getName());
                } else if (RobotEventEnum.FRIEND_MSG.equals(pluginParam.getRobotEventEnum())) {
                    PluginHookUtil.holdFriendPluginHook(pluginParam.getFrom(), pluginHook.getName());
                }
            }
        }
        return pluginResult;
    }

    /**
     * 插件执行（根据插件钩子名来获取钩子执行）
     *
     * @param pluginHookName
     * @param pluginParam
     * @return
     */
    private PluginResult executePluginHook(String pluginHookName, PluginParam pluginParam) {
        PluginHook pluginHook = PluginHookRegistrar.getPluginHookByName(pluginHookName);
        if (pluginHook == null) {
            return null;
        }
        return executePluginHook(pluginHook, pluginParam);
    }
}
