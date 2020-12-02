package com.molicloud.mqr.plugin.test;

import com.molicloud.mqr.plugin.core.PluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试插件
 * 仅供开发测试使用
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/02 9:52 上午
 */
@Slf4j
@Component
public class TestPluginExecutor implements PluginExecutor {

    @PHook(name = "Test", equalsKeywords = {
            "test",
    }, robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setMessage("test");
        return pluginResult;
    }
}
