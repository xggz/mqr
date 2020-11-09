package com.molicloud.mqr.framework.handler;

import kotlin.coroutines.Continuation;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.LoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.util.Scanner;

/**
 * 机器人登录验证处理器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:21 下午
 */
@Slf4j
public class LoginVerifyHandler extends LoginSolver {

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] pic, @NotNull Continuation<? super String> continuation) {
        FileImageOutputStream imageOutput = null;
        try {
            imageOutput = new FileImageOutputStream(new File(bot.getId() + "-" + System.currentTimeMillis() + ".png"));
            imageOutput.write(pic, 0, pic.length);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (imageOutput != null) {
                try {
                    imageOutput.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.info("请输入 4 位字母验证码. 若要更换验证码, 请直接回车");
        return new Scanner(System.in).nextLine();
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        log.info("请在任意浏览器中打开以下链接并完成验证码");
        log.info(url);
        log.info("完成后请输入任意字符");
        return new Scanner(System.in).nextLine();
    }

    @Nullable
    @Override
    public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
        log.info("需要进行账户安全认证");
        log.info("该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题");
        log.info("请将该链接在QQ浏览器中打开并完成认证, 成功后输入任意字符");
        log.info(url);
        return new Scanner(System.in).nextLine();
    }
}
