package com.molicloud.mqr.controller;

import com.molicloud.mqr.common.define.RobotAllowList;
import com.molicloud.mqr.common.rest.Res;
import com.molicloud.mqr.common.define.RobotVerify;
import com.molicloud.mqr.common.dto.RobotInfoDto;
import com.molicloud.mqr.common.enums.RobotStateEnum;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.common.setting.RobotInfo;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 设置相关操作接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 9:58 上午
 */
@RestController
@RequestMapping("/api/sys-setting")
public class SettingController {

    @Autowired
    private SysSettingService sysSettingService;

    @GetMapping("/robot-info")
    public Res<RobotInfoVo> getRobotInfo() {
        return Res.success(sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class));
    }

    @PostMapping("/robot-info")
    public Res saveRobotInfo(@RequestBody RobotInfoDto dto) {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (robotInfoVo != null && !RobotStateEnum.NOT_ENABLED.getValue().equals(robotInfoVo.getState())) {
            return Res.failed("请先停止机器人运行后再编辑");
        }
        return Res.success(sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, dto, RobotInfo.class));
    }

    @GetMapping("/robot-verify")
    public Res<RobotVerify> getRobotVerify() {
        return Res.success(sysSettingService.getSysSettingByName(SettingEnum.ROBOT_LOGIN_VERIFY, RobotVerify.class));
    }

    @PostMapping("/robot-verify")
    public Res saveRobotVerify(@RequestParam("code") String code) {
        return Res.success(sysSettingService.saveSysSetting(SettingEnum.ROBOT_LOGIN_VERIFY_RESULT, code, String.class));
    }

    @GetMapping("/robot-allow-list")
    public Res<RobotAllowList> getRobotAllowList() {
        return Res.success(sysSettingService.getSysSettingByName(SettingEnum.ROBOT_ALLOW_LIST, RobotAllowList.class));
    }

    @PostMapping("/robot-allow-list")
    public Res saveRobotAllowList(@RequestBody RobotAllowList robotAllowList) {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (robotInfoVo != null && !RobotStateEnum.NOT_ENABLED.getValue().equals(robotInfoVo.getState())) {
            return Res.failed("请先停止机器人运行后再编辑");
        }
        return Res.success(sysSettingService.saveSysSetting(SettingEnum.ROBOT_ALLOW_LIST, robotAllowList, RobotAllowList.class));
    }
}
