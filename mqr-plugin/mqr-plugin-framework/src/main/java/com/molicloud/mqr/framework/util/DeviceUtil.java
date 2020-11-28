package com.molicloud.mqr.framework.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import com.molicloud.mqr.plugin.core.RobotContextHolder;
import com.molicloud.mqr.framework.common.DeviceInfo;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * 机器人终端设备工具类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:11 下午
 */
@UtilityClass
public class DeviceUtil {

    /**
     * 获取机器人设备信息的JSON字符串
     *
     * @return
     */
    public String getDeviceInfoJson() {
        // 设备信息文件
        File file = new File("deviceInfo-".concat(RobotContextHolder.getRobot().getQq()).concat(".json"));
        String deviceInfoJson = null;
        if (file.exists()) {
            FileReader fileReader = new FileReader(file);
            deviceInfoJson = fileReader.readString();
        } else {
            deviceInfoJson = new JSONObject(new DeviceInfo()).toString();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(deviceInfoJson);
        }
        return deviceInfoJson;
    }
}
