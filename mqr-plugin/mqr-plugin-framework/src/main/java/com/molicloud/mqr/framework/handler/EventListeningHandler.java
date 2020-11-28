package com.molicloud.mqr.framework.handler;

import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.define.AtDef;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.framework.event.PluginResultEvent;
import com.molicloud.mqr.framework.util.PluginUtil;
import com.molicloud.mqr.service.SysSettingService;
import kotlin.coroutines.CoroutineContext;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Autowired
    private SysSettingService sysSettingService;

    /**
     * 监听群消息
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus onGroupMessage(GroupMessageEvent event) {
        // 实例化插件入参对象
        PluginParam pluginParam = new PluginParam();
        pluginParam.setFrom(String.valueOf(event.getSender().getId()));
        pluginParam.setTo(String.valueOf(event.getGroup().getId()));
        pluginParam.setData(event.getMessage().contentToString());
        pluginParam.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        // 获取消息中的At信息
        Set<AtDef> atDefSet = new LinkedHashSet<>();
        boolean isAt = getAtInfo(event.getMessage(), String.valueOf(event.getBot().getId()), atDefSet);
        pluginParam.setAt(isAt);
        pluginParam.setAts(atDefSet);
        // 获取机器人管理员账号
        pluginParam.setAdmins(robotAdmins());
        // 处理消息事件
        handlerMessageEvent(pluginParam);
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
        // 实例化插件入参对象
        PluginParam pluginParam = new PluginParam();
        pluginParam.setFrom(String.valueOf(event.getFriend().getId()));
        pluginParam.setTo(String.valueOf(event.getBot().getId()));
        pluginParam.setData(event.getMessage().contentToString());
        pluginParam.setRobotEventEnum(RobotEventEnum.FRIEND_MSG);
        // 获取机器人管理员账号
        pluginParam.setAdmins(robotAdmins());
        // 处理消息事件
        handlerMessageEvent(pluginParam);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    @Override
    public void handleException(CoroutineContext context, Throwable exception) {
        throw new RuntimeException("在事件处理中发生异常", exception);
    }

    /**
     * 处理消息事件
     *
     * @param pluginParam
     */
    private void handlerMessageEvent(PluginParam pluginParam) {
        // 封装插件结果处理事件
        PluginResultEvent pluginResultEvent = new PluginResultEvent();
        pluginResultEvent.setPluginParam(pluginParam);
        // 执行插件，执行成功则推送异步处理的事件
        if (PluginUtil.executePlugin(pluginResultEvent)) {
            eventPublisher.publishEvent(pluginResultEvent);
        }
    }

    /**
     * 获取机器人管理员列表
     *
     * @return
     */
    private String[] robotAdmins() {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (robotInfoVo == null) {
            return new String[]{};
        }
        return robotInfoVo.getAdmins();
    }

    /**
     * 获取消息中的所有At信息，并返回机器人是否被At
     *
     * @param messageChain
     * @param rid
     * @param atDefSet
     * @return
     */
    private boolean getAtInfo(MessageChain messageChain, String rid, Set<AtDef> atDefSet) {
        AtomicBoolean result = new AtomicBoolean(false);
        messageChain.forEach(message -> {
            if (message instanceof At) {
                At at = (At) message;
                if (rid.equals(String.valueOf(at.getTarget()))) {
                    result.set(true);
                }
                AtDef atDef = new AtDef();
                atDef.setId(String.valueOf(at.getTarget()));
                atDef.setNick(at.getDisplay());
                atDefSet.add(atDef);
            }
        });
        return result.get();
    }
}