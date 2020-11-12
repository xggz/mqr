package com.molicloud.mqr.framework.event;

import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import com.molicloud.mqr.framework.properties.RobotProperties;
import net.mamoe.mirai.Bot;
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
        // 解析事件内容
        String ownerId = pluginResultEvent.getOwnerId();
        RobotEventEnum robotEventEnum = pluginResultEvent.getRobotEventEnum();
        PluginResult pluginResult = pluginResultEvent.getPluginResult();
        Object resultContent = pluginResult.getData();
        switch (robotEventEnum) {
            case GROUP_MSG:
                if (resultContent instanceof String) {
                    bot.getGroup(Long.parseLong(ownerId)).sendMessage(String.valueOf(resultContent));
                }
                break;
            case FRIEND_MSG:
                if (resultContent instanceof String) {
                    bot.getFriend(Long.parseLong(ownerId)).sendMessage(String.valueOf(resultContent));
                }
                break;
            default:
                break;
        }

    }
}