package com.molicloud.mqr.framework;

import com.molicloud.mqr.enums.RobotStateEnum;
import com.molicloud.mqr.enums.SettingEnum;
import com.molicloud.mqr.framework.handler.EventListeningHandler;
import com.molicloud.mqr.framework.handler.LoginVerifyHandler;
import com.molicloud.mqr.framework.util.DeviceUtil;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.setting.RobotInfo;
import com.molicloud.mqr.vo.RobotInfoVo;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        RobotContextHolder.setRobotInfo(robotInfoVo);
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
        // 修改机器人状态
        RobotInfo robotInfo = new RobotInfo();
        BeanUtils.copyProperties(robotInfoVo, robotInfo);
        try {
            // 登录QQ
            bot.login();
            robotInfo.setState(RobotStateEnum.ONLINE.getValue());
        } catch (Exception e) {
            robotInfo.setState(RobotStateEnum.NOT_ENABLED.getValue());
            log.error(e.getMessage(), e);
        }
        sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, robotInfo, RobotInfo.class);
        // 如果机器人已在线，则注册事件监听
        if (RobotStateEnum.ONLINE.getValue().equals(robotInfo.getState())) {
            RobotContextHolder.setState(RobotStateEnum.ONLINE.getValue());
            // 注册QQ机器人事件监听
            Events.registerEvents(bot, eventListeningHandler);
            // 阻塞当前线程直到 bot 离线
            bot.join();
        }
    }
}