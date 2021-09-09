package com.molicloud.mqr.framework.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.molicloud.mqr.common.define.RobotVerify;
import com.molicloud.mqr.common.enums.RobotStateEnum;
import com.molicloud.mqr.common.enums.RobotVerifyEnum;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.service.SysSettingService;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import kotlin.coroutines.Continuation;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.LoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 机器人登录验证处理器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:21 下午
 */
@Slf4j
@Component
public class LoginVerifyHandler extends LoginSolver {

    @Autowired
    private SysSettingService sysSettingService;

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] pic, @NotNull Continuation<? super String> continuation) {
        log.info("请输入 4 位字母验证码");
        return handlerVerify(RobotVerifyEnum.IMG, Base64.encode(pic));
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        log.info("请用户QQ扫码二维码完成验证，然后在控制台提交验证");
        log.info(url);
        log.info("完成后请输入任意字符");
        return handlerVerify(RobotVerifyEnum.URL, Base64.encode(QrCodeUtil.generatePng(url, new QrConfig())));
    }

    @Nullable
    @Override
    public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        log.info("需要进行账户安全认证");
        log.info("该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题");
        log.info("请用户QQ扫码二维码完成验证，然后在控制台提交验证");
        log.info(url);
        return handlerVerify(RobotVerifyEnum.URL, Base64.encode(QrCodeUtil.generatePng(url, new QrConfig())));
    }

    /**
     * 处理登录验证
     *
     * @param robotVerifyEnum
     * @param content
     * @return
     */
    private String handlerVerify(RobotVerifyEnum robotVerifyEnum, String content) {
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