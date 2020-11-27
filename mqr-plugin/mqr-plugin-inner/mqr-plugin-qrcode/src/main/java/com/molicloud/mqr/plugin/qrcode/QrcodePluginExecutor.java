package com.molicloud.mqr.plugin.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.molicloud.mqr.plugin.core.PluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.message.Img;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 生成二维码插件
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/11/25 5:08 下午
 */
@Slf4j
@Component
public class QrcodePluginExecutor implements PluginExecutor {

    @PHook(name = "Qrcode", keywords = {
            "生成二维码", "二维码生成"
    }, robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) throws IOException, WriterException {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);

        if (ExecuteTriggerEnum.KEYWORD.equals(pluginParam.getExecuteTriggerEnum())) {
            pluginResult.setHold(true);
            pluginResult.setMessage("请发送二维码文本内容，字符不能过长喔～");
        } else if (ExecuteTriggerEnum.HOLD.equals(pluginParam.getExecuteTriggerEnum())) {
            String message = String.valueOf(pluginParam.getData());
            pluginResult.setHold(false);
            pluginResult.setMessage(new Img(getQRCodeImage(message, 300, 300)));
        }
        return pluginResult;
    }

    /**
     * 生成二维码，返回字节流
     *
     * @param text   二维码需要包含的信息
     * @param width  二维码宽度
     * @param height 二维码高度
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static InputStream getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return new ByteArrayInputStream(pngData);
    }
}
