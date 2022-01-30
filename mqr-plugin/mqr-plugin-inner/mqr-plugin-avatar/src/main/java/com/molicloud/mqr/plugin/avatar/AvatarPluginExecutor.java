package com.molicloud.mqr.plugin.avatar;

import cn.hutool.core.img.BackgroundRemoval;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
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
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    private static BufferedImage christmasImage = null;
    private static List<BufferedImage> tigerFileImageList = new ArrayList<>();

    static {
        try {
            File configFile = FileUtil.file("gq-left2.png");
            if (!configFile.exists()) {
                InputStream in = AvatarPluginExecutor.class.getClassLoader().getResourceAsStream("/gq-left2.png");
                FileUtil.writeFromStream(in, configFile);
            }
            pressImage = ImgUtil.read(configFile);

            File christmasFile = FileUtil.file("christmas1.png");
            if (!christmasFile.exists()) {
                InputStream in = AvatarPluginExecutor.class.getClassLoader().getResourceAsStream("/christmas1.png");
                FileUtil.writeFromStream(in, christmasFile);
            }
            christmasImage = ImgUtil.read(christmasFile);

            for (int i = 1; i <= 30; i++) {
                File tigerFile = FileUtil.file("tiger/" + i + ".png");
                if (!tigerFile.exists()) {
                    InputStream in = AvatarPluginExecutor.class.getClassLoader().getResourceAsStream("tiger/" + i + ".png");
                    FileUtil.writeFromStream(in, tigerFile);
                }
                tigerFileImageList.add(ImgUtil.read(tigerFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PHook(name = "Avatar",
            equalsKeywords = { "我的国旗头像", "我的灰色头像", "我的黑白头像", "我的圣诞头像", "我的圣诞节头像", "我的虎年头像" },
            startsKeywords = { "生成国旗头像", "生成灰色头像", "生成黑白头像", "生成圣诞头像", "生成圣诞节头像", "生成虎年头像" },
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
        } else if (AvatarType.CHRISTMAS.equals(avatarType)) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage(new Img(christmasAvatar(srcImage, fromId)));
        } else if (AvatarType.TIGER.equals(avatarType)) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage(new Img(tigerAvatar(srcImage, fromId)));
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
        } else if (keyword.equalsIgnoreCase("生成圣诞头像")
                || keyword.equalsIgnoreCase("我的圣诞头像")
                || keyword.equalsIgnoreCase("生成圣诞节头像")
                || keyword.equalsIgnoreCase("我的圣诞节头像")) {
            return AvatarType.CHRISTMAS;
        } else if (keyword.equalsIgnoreCase("生成虎年头像")
                || keyword.equalsIgnoreCase("我的虎年头像")) {
            return AvatarType.TIGER;
        }

        return null;
    }

    private String findFromId(PluginParam pluginParam) {
        if (pluginParam.getKeyword().equals("生成国旗头像")
                || pluginParam.getKeyword().equals("生成灰色头像")
                || pluginParam.getKeyword().equals("生成黑白头像")
                || pluginParam.getKeyword().equals("生成圣诞头像")
                || pluginParam.getKeyword().equals("生成圣诞节头像")
                || pluginParam.getKeyword().equals("生成虎年头像")) {
            String message = (String) pluginParam.getData();
            String[] info = message.split("生成国旗头像");
            if (info.length != 2) {
                info = message.split("生成灰色头像");
            }
            if (info.length != 2) {
                info = message.split("生成黑白头像");
            }
            if (info.length != 2) {
                info = message.split("生成圣诞头像");
            }
            if (info.length != 2) {
                info = message.split("生成圣诞节头像");
            }
            if (info.length != 2) {
                info = message.split("生成虎年头像");
            }

            if (StrUtil.isNotBlank(info[1])) {
                if (NumberUtil.isNumber(StrUtil.trim(info[1]))) {
                    return StrUtil.trim(info[1]);
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

    private File tigerAvatar(BufferedImage srcImage, String fromId) {
        int srcImgWidth = srcImage.getWidth();
        int srcImgHeight = srcImage.getHeight();

        int scaleSize = srcImgWidth < srcImgHeight ? srcImgWidth : srcImgHeight;
        int x = (srcImgWidth - scaleSize) / 2;
        int y = (srcImgHeight - scaleSize) / 2;
        x = x > 0 ? -x : 0;
        y = y > 0 ? -y : 0;

        Integer random = RandomUtil.randomInt(tigerFileImageList.size());
        Image pressScaleImage = ImgUtil.scale(tigerFileImageList.get(random), scaleSize, scaleSize);

        File dest = FileUtil.file("dest-"+fromId+"-tiger+" + random + ".png");

        ImgUtil.pressImage(
                srcImage,
                dest,
                pressScaleImage,
                x,
                y,
                0.9f
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

    private File christmasAvatar(BufferedImage srcImage, String fromId) {
        int size = 393;
        int srcImgWidth = srcImage.getWidth();
        int srcImgHeight = srcImage.getHeight();
        String srcMainColor = BackgroundRemoval.getMainColor(srcImage);

        Image fixedImage;
        if (srcImgWidth < srcImgHeight) {
            int height = new BigDecimal(size)
                    .multiply(new BigDecimal(srcImgHeight))
                    .divide(new BigDecimal(srcImgWidth), 0, RoundingMode.HALF_UP)
                    .intValue();
            fixedImage = ImgUtil.scale(srcImage, size, height);
        } else {
            int width = new BigDecimal(size)
                    .multiply(new BigDecimal(srcImgWidth))
                    .divide(new BigDecimal(srcImgHeight), 0, RoundingMode.HALF_UP)
                    .intValue();
            fixedImage = ImgUtil.scale(srcImage, width, size);
        }

        BufferedImage avatarImage = ImgUtil.toBufferedImage(fixedImage);
        int avatarWidth = avatarImage.getWidth();
        int avatarHeight = avatarImage.getHeight();

        int scaleSize = avatarWidth < avatarHeight ? avatarWidth : avatarHeight;
        int x = (avatarWidth - scaleSize) / 2;
        int y = (avatarHeight - scaleSize) / 2;
        x = x > 0 ? -x : 0;
        y = y > 0 ? -y : 0;

        int newSize = scaleSize + 140;
        Image christmasScaleImage = ImgUtil.scale(christmasImage, newSize, newSize);

        Image newImage = ImgUtil.pressImage(
                restImages(avatarImage, srcMainColor),
                christmasScaleImage,
                x,
                y-1,
                1.0f
        );

        BufferedImage createImage = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D create2d = createImage.createGraphics();
        create2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        create2d.drawImage(newImage, 0, 0, null);
        create2d.dispose();

//        for (int y1 = createImage.getMinY(); y1 < createImage.getHeight(); y1++) {
//            for (int x1 = createImage.getMinX(); x1 < createImage.getWidth(); x1++) {
//                // 获取像素的16进制
//                int rgb = createImage.getRGB(x1, y1);
//                if ((rgb & 0xff0000) >> 16 == 250 && (rgb & 0xff00) >> 8 == 251 && (rgb & 0xff) == 252) {
//                    createImage.setRGB(x1, y1, (1 << 24) | (rgb & 0x00ffffff));
//                }
//            }
//        }

        File dest = FileUtil.file("dest-"+fromId+"-christmas.png");
        ImgUtil.write(createImage, dest);
        return dest;
    }

    /**
     * 重置头像，变圆，扩大，增加背景色
     *
     * @param avatarImage
     * @return
     */
    public BufferedImage restImages(BufferedImage avatarImage, String srcMainColor) {
        try {
            int size = avatarImage.getWidth();

            BufferedImage formatAvatarImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = formatAvatarImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.clip(new Ellipse2D.Double(0, 0, size, size));
            graphics.drawImage(avatarImage, 0, 0, size, size, null);
            graphics.dispose();

            int border = 200;
            BufferedImage boxAvatarImage = new BufferedImage(size + border, size + border, BufferedImage.TYPE_INT_RGB);
            Graphics2D box2d = boxAvatarImage.createGraphics();
            // 设置背景色，之后再擦除（这里不加背景色，与模板框结合效果不佳）
            box2d.setBackground(ImgUtil.hexToColor(srcMainColor));
            box2d.clearRect(0, 0, size + border, size + border);
            box2d.drawImage(formatAvatarImage, border / 2, (border / 2) + 10, size, size, null);
            box2d.dispose();

            return boxAvatarImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}