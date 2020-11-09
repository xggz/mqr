package com.molicloud.mqr.framework.config;

import com.molicloud.mqr.framework.handler.LoginVerifyHandler;
import com.molicloud.mqr.framework.util.DeviceUtil;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * 机器人配置
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:14 下午
 */
public class RobotConfiguration extends BotConfiguration {

    private RobotConfiguration() {}

    public RobotConfiguration(Long qq) {
        super();

        /**
         * 加载设备信息
         */
        loadDeviceInfoJson(DeviceUtil.getDeviceInfoJson(qq));

        /**
         * 自定义登录验证处理器
         */
        setLoginSolver(new LoginVerifyHandler());

        /**
         * 不输出网络日志
         */
//        noNetworkLog();

        /**
         * 不输出机器人日志
         */
//        noBotLog();
    }
}
