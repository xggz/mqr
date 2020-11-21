package com.molicloud.mqr.controller;

import com.molicloud.mqr.common.rest.Res;
import com.molicloud.mqr.dto.RobotInfoDto;
import com.molicloud.mqr.enums.SettingEnums;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.setting.RobotInfo;
import com.molicloud.mqr.vo.RobotInfoVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 设置相关操作接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 9:58 上午
 */
@RestController
@RequestMapping("/sys-setting")
@Api(value = "SettingApi", tags = "设置相关操作接口")
public class SettingController {

    @Autowired
    private SysSettingService sysSettingService;

    @GetMapping("/robot-info")
    public Res<RobotInfoVo> getRobotInfo() {
        return Res.success(sysSettingService.getSysSettingByName(SettingEnums.ROBOT_INFO, RobotInfoVo.class));
    }

    @PostMapping("/robot-info")
    public Res saveRobotInfo(@RequestBody RobotInfoDto dto) {
        return Res.success(sysSettingService.saveSysSetting(SettingEnums.ROBOT_INFO, dto, RobotInfo.class));
    }
}