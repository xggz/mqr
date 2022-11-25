package com.molicloud.mqr.framework.handler;

import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.define.RobotVerify;
import com.molicloud.mqr.common.enums.RobotStateEnum;
import com.molicloud.mqr.common.enums.RobotVerifyEnum;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import com.molicloud.mqr.service.SysSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 登录验证服务
 * 
 * @author xggz yyimba@qq.com
 * @date 2022/11/25 10:44
 */
@Slf4j
@Service
public class LoginVerfyService {

    @Autowired
    private SysSettingService sysSettingService;

    /**
     * 处理登录验证
     *
     * @param robotVerifyEnum
     * @param content
     * @return
     */
    public String handlerVerify(RobotVerifyEnum robotVerifyEnum, String content) {
        // 修改机器人状态
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        robotInfoVo.setState(RobotStateEnum.TO_VERIFIED.getValue());
        sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, robotInfoVo, RobotInfoVo.class);
        // 保存机器人的登录验证信息
        RobotVerify robotVerify = new RobotVerify();
        robotVerify.setType(robotVerifyEnum.name());
        robotVerify.setContent(content);
        sysSettingService.saveSysSetting(SettingEnum.ROBOT_LOGIN_VERIFY, robotVerify, RobotVerify.class);
        // 线程阻塞获取验证结果
        Future<String> future = Executors.newSingleThreadExecutor().submit(() -> {
            String code = "";
            do {
                code = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_LOGIN_VERIFY_RESULT, String.class);
                if (StrUtil.isBlank(code)) {
                    log.debug("等待人工验证中。。。。。");
                    Thread.sleep(2000L);
                }
                // 查询最新的机器人状态
                RobotInfoVo riv = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
                if (!RobotStateEnum.TO_VERIFIED.getValue().equals(riv.getState())) {
                    return null;
                }
            } while (StrUtil.isBlank(code));
            return code;
        });

        String result = "";
        try {
            result = future.get();
            if (StrUtil.isNotBlank(result)) {
                // 获取登录结果后，修改机器人状态为登录中
                robotInfoVo.setState(RobotStateEnum.LOGGING.getValue());
                sysSettingService.saveSysSetting(SettingEnum.ROBOT_INFO, robotInfoVo, RobotInfoVo.class);
            }
            // 重置登录验证结果
            sysSettingService.saveSysSetting(SettingEnum.ROBOT_LOGIN_VERIFY_RESULT, "", String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}