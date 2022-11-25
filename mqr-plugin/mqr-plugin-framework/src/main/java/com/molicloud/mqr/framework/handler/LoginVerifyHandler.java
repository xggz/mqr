package com.molicloud.mqr.framework.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.molicloud.mqr.common.enums.RobotVerifyEnum;
import kotlin.coroutines.Continuation;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 机器人登录验证处理器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:21 下午
 */
@Slf4j
@Component
public class LoginVerifyHandler extends AbstractLoginVerifyHandler {

    @Autowired
    private LoginVerfyService loginVerfyService;

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] pic, @NotNull Continuation<? super String> continuation) {
        log.info("请输入 4 位字母验证码");
        return loginVerfyService.handlerVerify(RobotVerifyEnum.IMG, Base64.encode(pic));
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        log.info("请新窗口打开验证地址，然后在控制台提交验证");
        log.info(url);
        log.info("完成后请输入验证结果");
        return loginVerfyService.handlerVerify(RobotVerifyEnum.SLIDER, url);
    }

    @Nullable
    @Override
    public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        log.info("需要进行账户安全认证");
        log.info("该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题");
        log.info("请用户QQ扫码二维码完成验证，然后在控制台提交验证");
        log.info(url);
        return loginVerfyService.handlerVerify(RobotVerifyEnum.QRCODE, Base64.encode(QrCodeUtil.generatePng(url, new QrConfig())));
    }

    @Override
    public boolean isSliderCaptchaSupported() {
        // 支持滑块验证登录
        return true;
    }
}