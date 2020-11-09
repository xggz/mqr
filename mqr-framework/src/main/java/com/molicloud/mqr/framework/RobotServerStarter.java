package com.molicloud.mqr.framework;

import com.molicloud.mqr.framework.config.RobotConfiguration;
import com.molicloud.mqr.framework.handler.EventListeningHandler;
import com.molicloud.mqr.framework.properties.RobotProperties;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 机器人服务启动器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:25 下午
 */
@Component
public class RobotServerStarter {

    @Autowired
    private RobotProperties robotProperties;

    @Autowired
    private EventListeningHandler eventListeningHandler;

    /**
     * 启动机器人服务
     */
    public void start() {
        // 初始化机器人对象
        final Bot bot = BotFactoryJvm.newBot(robotProperties.getQq(), robotProperties.getPassword(), new RobotConfiguration(robotProperties.getQq()));
        // 登录QQ
        bot.login();
        // 注册QQ机器人事件监听
        Events.registerEvents(bot, eventListeningHandler);
        // 阻塞当前线程直到 bot 离线
        bot.join();
    }
}
