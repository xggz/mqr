package com.molicloud.mqr.runner;

import com.molicloud.mqr.enums.RobotStateEnum;
import com.molicloud.mqr.enums.SettingEnum;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.setting.RobotInfo;
import com.molicloud.mqr.vo.RobotInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 机器人状态初始化
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/24 4:43 下午
 */
@Component
public class RobotInitRunner implements CommandLineRunner {

    @Autowired
    private SysSettingService sysSettingService;

    @Override
    public void run(String... args) throws Exception {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (robotInfoVo != null) {
            // 每次程序启动时，初始化机器人状态为未登录
            RobotInfo robotInfo = new RobotInfo();
            BeanUtils.copyProperties(robotInfoVo, robotInfo);
            robotInfo.setState(RobotStateEnum.NOT_ENABLED.getValue());
            sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, robotInfo, RobotInfo.class);
        }
    }
}