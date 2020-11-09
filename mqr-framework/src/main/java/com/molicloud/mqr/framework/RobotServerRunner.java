package com.molicloud.mqr.framework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 运行QQ机器人服务
 *
 * @author feitao yyimba@qq.com
 * @since 2020/10/9 4:45 下午
 */
@Slf4j
@Component
public class RobotServerRunner implements CommandLineRunner {

    @Autowired
    private RobotServerStarter robotServerStarter;

    @Override
    public void run(String... args) throws Exception {
        // 启动机器人运行线程
        Thread qqRunThread = new Thread(() -> {
            robotServerStarter.start();
        });
        qqRunThread.setDaemon(true);
        qqRunThread.setName("QQ机器人服务运行线程");
        qqRunThread.start();
    }
}
