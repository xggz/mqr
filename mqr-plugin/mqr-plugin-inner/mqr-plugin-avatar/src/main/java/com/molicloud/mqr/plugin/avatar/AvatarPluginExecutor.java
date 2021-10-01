package com.molicloud.mqr.plugin.avatar;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.message.make.Img;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * 头像生成插件
 *
 * @author feitao yyimba@qq.com
 * @since 2021/10/1 11:10 上午
 */
@Slf4j
@Component
public class AvatarPluginExecutor extends AbstractPluginExecutor {

    @PHook(name = "Avatar",
            equalsKeywords = { "我的国旗头像" },
            startsKeywords = { "生成国旗头像" },
            robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);

        String nk = pluginParam.getFrom();
        if (pluginParam.getKeyword().equals("生成国旗头像")) {
            String message = (String) pluginParam.getData();
            String[] info = message.split("生成国旗头像");
            if (!StrUtil.isBlank(info[1]) && !NumberUtil.isNumber(info[1])) {
                pluginResult.setMessage("qq格式不正确");
                return pluginResult;
            }
            nk = info[1];
        }

        URL url = URLUtil.url("https://q4.qlogo.cn/g?b=qq&nk="+nk+"&s=640");
        BufferedImage srcImage = ImgUtil.read(url);

        int srcImgWidth = srcImage.getWidth();
        int srcImgHeight = srcImage.getHeight();

        BufferedImage pressImage = ImgUtil.read("gq-left2.png");

        int scaleSize = srcImgWidth < srcImgHeight ? srcImgWidth : srcImgHeight;
        int x = (srcImgWidth - scaleSize) / 2;
        int y = (srcImgHeight - scaleSize) / 2;
        x = x > 0 ? -x : 0;
        y = y > 0 ? -y : 0;

        Image pressScaleImage = ImgUtil.scale(pressImage, scaleSize, scaleSize);

        File dest = FileUtil.file("dest-"+pluginParam.getFrom()+"-"+System.currentTimeMillis()+".jpg");

        ImgUtil.pressImage(
                srcImage,
                dest,
                pressScaleImage,
                x,
                y,
                0.85f
        );

        pluginResult.setMessage(new Img(dest));
        return pluginResult;
    }
}