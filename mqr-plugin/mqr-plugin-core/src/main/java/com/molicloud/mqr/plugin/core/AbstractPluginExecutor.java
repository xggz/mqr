package com.molicloud.mqr.plugin.core;

import com.molicloud.mqr.plugin.core.define.RobotDef;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 插件执行器抽象接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 1:46 下午
 */
@Component
public abstract class AbstractPluginExecutor implements PluginExecutor {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 插件主动推送消息事件
     *
     * @param messageEvent
     */
    protected void pushMessage(MessageEvent messageEvent) {
        eventPublisher.publishEvent(messageEvent);
    }

    /**
     * 获取管理ID列表
     *
     * @return
     */
    protected String[] getAdmins() {
        return RobotContextHolder.getRobot().getAdmins();
    }

    /**
     * 获取群列表
     *
     * @return
     */
    protected List<RobotDef.Member> getGroupList() {
        return RobotContextHolder.getRobot().getGroupList();
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    protected List<RobotDef.Member> getFriendList() {
        return RobotContextHolder.getRobot().getFriendList();
    }
}