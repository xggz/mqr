package com.molicloud.mqr.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.rest.Res;
import com.molicloud.mqr.common.enums.RobotStateEnum;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.framework.PluginJobHandler;
import com.molicloud.mqr.framework.RobotServerStarter;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 机器人相关操作接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/23 11:18 上午
 */
@Slf4j
@RestController
@RequestMapping("/api/robot")
public class RobotController {

    @Autowired
    private SysSettingService sysSettingService;

    @Autowired
    private RobotServerStarter robotServerStarter;

    @Autowired
    private PluginJobHandler pluginJobHandler;

    @GetMapping("/start")
    public Res start() {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (!checkRobotInfo(robotInfoVo)) {
            return Res.failed("机器人信息配置不全面");
        }
        // 暂时只支持数字qq号登录
        if (!NumberUtil.isNumber(robotInfoVo.getQq())) {
            return Res.failed("机器人QQ号码不合法");
        }
        // 判断机器人当前状态是否可以启动运行
        if (!RobotStateEnum.isCanRun(robotInfoVo.getState())) {
            return Res.failed("当前机器人运行中，请先停止当前机器人的运行再启动");
        }

        // 启动机器人
        Thread qqRunThread = new Thread(() -> {
            robotServerStarter.start(robotInfoVo);
        });
        qqRunThread.setDaemon(true);
        qqRunThread.setName("QQ机器人服务运行线程");
        qqRunThread.start();

        return Res.success();
    }

    @GetMapping("/stop")
    public Res stop() {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (robotInfoVo != null) {
            if (RobotStateEnum.TO_VERIFIED.getValue().equals(robotInfoVo.getState())
                    || RobotStateEnum.LOGGING.getValue().equals(robotInfoVo.getState())) {
                return Res.failed("登录中的机器人无法停止");
            }
            try {
                // 关闭机器人运行
                Bot.getInstances().stream().forEach(bot -> bot.close(null));
                // 取消所有计划任务
                pluginJobHandler.cancelAllTriggerTask();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            robotInfoVo.setState(RobotStateEnum.NOT_ENABLED.getValue());
            sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, robotInfoVo, RobotInfoVo.class);
        }
        return Res.success();
    }

    /**
     * 检查机器人信息是否合法
     *
     * @param vo
     * @return
     */
    private boolean checkRobotInfo(RobotInfoVo vo) {
        return !(vo == null || StrUtil.isBlank(vo.getQq()) || StrUtil.isBlank(vo.getPassword()) || vo.getState() == null);
    }
}
