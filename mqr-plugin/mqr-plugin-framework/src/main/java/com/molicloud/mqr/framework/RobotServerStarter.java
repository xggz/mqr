package com.molicloud.mqr.framework;

import com.molicloud.mqr.common.enums.RobotStateEnum;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.framework.common.PluginJob;
import com.molicloud.mqr.framework.handler.EventListeningHandler;
import com.molicloud.mqr.framework.handler.LoginVerifyHandler;
import com.molicloud.mqr.framework.util.DeviceUtil;
import com.molicloud.mqr.plugin.core.RobotContextHolder;
import com.molicloud.mqr.plugin.core.define.RobotDef;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器人服务启动器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:25 下午
 */
@Slf4j
@Component
public class RobotServerStarter {

    @Autowired
    private EventListeningHandler eventListeningHandler;

    @Autowired
    private PluginJobHandler pluginJobHandler;

    @Autowired
    private LoginVerifyHandler loginVerifyHandler;

    @Autowired
    private SysSettingService sysSettingService;

    /**
     * 启动机器人服务
     *
     * @param robotInfoVo
     */
    public void start(RobotInfoVo robotInfoVo) {
        // 设置线程上下文中的机器人信息
        RobotDef robotDef = new RobotDef();
        BeanUtils.copyProperties(robotInfoVo, robotDef);
        RobotContextHolder.setRobot(robotDef);
        // 初始化机器人对象
        final Bot bot = BotFactoryJvm.newBot(Long.parseLong(robotInfoVo.getQq()), robotInfoVo.getPassword(), new BotConfiguration(){
            {
                /**
                 * 加载设备信息
                 */
                loadDeviceInfoJson(DeviceUtil.getDeviceInfoJson());

                /**
                 * 自定义登录验证处理器
                 */
                setLoginSolver(loginVerifyHandler);

                /**
                 * 不输出网络日志
                 */
                // noNetworkLog();

                /**
                 * 不输出机器人日志
                 */
                // noBotLog();
            }
        });
        try {
            // 注册QQ机器人事件监听
            Events.registerEvents(bot, eventListeningHandler);
            // 登录QQ
            bot.login();
            robotInfoVo.setState(RobotStateEnum.ONLINE.getValue());
        } catch (Exception e) {
            robotInfoVo.setState(RobotStateEnum.NOT_ENABLED.getValue());
            log.error(e.getMessage(), e);
        }
        // 修改机器人状态
        sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, robotInfoVo, RobotInfoVo.class);
        // 如果机器人已在线，则注册事件监听
        if (RobotStateEnum.ONLINE.getValue().equals(robotInfoVo.getState())) {
            // 查找所有的群列表
            List<RobotDef.Group> groupList = bot.getGroups().stream().map(group -> new RobotDef.Group(String.valueOf(group.getId()), group.getName())).collect(Collectors.toList());
            robotDef.setGroupList(groupList);
            // 查找所有的好友列表
            List<RobotDef.Friend> friendList = bot.getFriends().stream().map(friend -> new RobotDef.Friend(String.valueOf(friend.getId()), friend.getNick())).collect(Collectors.toList());
            robotDef.setFriendList(friendList);
            // 保存最新的机器人信息
            RobotContextHolder.setRobot(robotDef);
            // 获取插件计划任务，并添加到任务调度
            List<PluginJob> pluginJobRepository = PluginExecutorRegistrar.getPluginJobRepository();
            pluginJobRepository.stream().forEach(pluginJob -> {
                pluginJobHandler.addTriggerTask(pluginJob.getName(), new TriggerTask(() -> {
                    try {
                        pluginJob.getPluginMethod().execute();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }, new CronTrigger(pluginJob.getCron())));
            });
            // 阻塞当前线程直到 bot 离线
            bot.join();
        }
    }
}