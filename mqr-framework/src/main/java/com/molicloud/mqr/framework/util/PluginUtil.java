package com.molicloud.mqr.framework.util;

import com.molicloud.mqr.common.PluginParam;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.PluginRegistrar;
import com.molicloud.mqr.framework.common.PluginHook;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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
     * 插件执行
     *
     * @param pluginHook
     * @param pluginParam
     * @return
     */
    public PluginResult execute(PluginHook pluginHook, PluginParam pluginParam) {
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
    public PluginResult execute(String pluginHookName, PluginParam pluginParam) {
        PluginHook pluginHook = PluginRegistrar.getPluginHookByName(pluginHookName);
        if (pluginHook == null) {
            return null;
        }
        return execute(pluginHook, pluginParam);
    }
}
