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
import java.io.InputStream;
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

    private static BufferedImage pressImage = null;

    static {
        try {
            File configFile = FileUtil.file("gq-left2.png");
            if (!configFile.exists()) {
                InputStream in = AvatarPluginExecutor.class.getClassLoader().getResourceAsStream("/gq-left2.png");
                FileUtil.writeFromStream(in, configFile);
            }
            pressImage = ImgUtil.read(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PHook(name = "Avatar",
            equalsKeywords = { "我的国旗头像", "我的灰色头像", "我的黑白头像" },
            startsKeywords = { "生成国旗头像", "生成灰色头像", "生成黑白头像" },
            robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();

        String fromId = findFromId(pluginParam);
        if (StrUtil.isBlank(fromId)) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage("QQ格式不正确");
            return pluginResult;
        }

        URL url = URLUtil.url("https://q4.qlogo.cn/g?b=qq&nk=" + fromId + "&s=640");
        BufferedImage srcImage = ImgUtil.read(url);

        AvatarType avatarType = findAvatarType(pluginParam);
        if (AvatarType.FLAG.equals(avatarType)) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage(new Img(guoqiAvatar(srcImage, fromId)));
        } else if (AvatarType.GRAY.equals(avatarType)) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage(new Img(grayAvatar(srcImage, fromId)));
        } else if (AvatarType.BINARY.equals(avatarType)) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage(new Img(binaryAvatar(srcImage, fromId)));
        }

        return pluginResult;
    }

    private AvatarType findAvatarType(PluginParam pluginParam) {
        String keyword = pluginParam.getKeyword();
        if (keyword.equalsIgnoreCase("生成国旗头像")
                || keyword.equalsIgnoreCase("我的国旗头像")) {
            return AvatarType.FLAG;
        } else if (keyword.equalsIgnoreCase("生成灰色头像")
                || keyword.equalsIgnoreCase("我的灰色头像")) {
            return AvatarType.GRAY;
        } else if (keyword.equalsIgnoreCase("生成黑白头像")
                || keyword.equalsIgnoreCase("我的黑白头像")) {
            return AvatarType.BINARY;
        }

        return null;
    }

    private String findFromId(PluginParam pluginParam) {
        if (pluginParam.getKeyword().equals("生成国旗头像")
                || pluginParam.getKeyword().equals("生成灰色头像")
                || pluginParam.getKeyword().equals("生成黑白头像")) {
            String message = (String) pluginParam.getData();
            String[] info = message.split("生成国旗头像");
            if (info.length != 2) {
                info = message.split("生成灰色头像");
            }
            if (info.length != 2) {
                info = message.split("生成黑白头像");
            }

            if (!StrUtil.isBlank(info[1])) {
                if (NumberUtil.isNumber(info[1])) {
                    return info[1];
                }
            }

            return null;
        }

        return pluginParam.getFrom();
    }

    private File guoqiAvatar(BufferedImage srcImage, String fromId) {
        int srcImgWidth = srcImage.getWidth();
        int srcImgHeight = srcImage.getHeight();

        int scaleSize = srcImgWidth < srcImgHeight ? srcImgWidth : srcImgHeight;
        int x = (srcImgWidth - scaleSize) / 2;
        int y = (srcImgHeight - scaleSize) / 2;
        x = x > 0 ? -x : 0;
        y = y > 0 ? -y : 0;

        Image pressScaleImage = ImgUtil.scale(pressImage, scaleSize, scaleSize);

        File dest = FileUtil.file("dest-"+fromId+"-guoqi.png");

        ImgUtil.pressImage(
                srcImage,
                dest,
                pressScaleImage,
                x,
                y,
                0.85f
        );

        return dest;
    }

    private File grayAvatar(BufferedImage srcImage, String fromId) {
        File dest = FileUtil.file("dest-"+fromId+"-gray.png");
        ImgUtil.gray(srcImage, dest);
        return dest;
    }

    private File binaryAvatar(BufferedImage srcImage, String fromId) {
        File dest = FileUtil.file("dest-"+fromId+"-binary.png");
        ImgUtil.binary(srcImage, dest);
        return dest;
    }
}