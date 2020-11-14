package com.molicloud.mqr.plugin.adblock;

import com.molicloud.mqr.common.plugin.PluginExecutor;
import com.molicloud.mqr.common.plugin.PluginParam;
import com.molicloud.mqr.common.plugin.PluginResult;
import com.molicloud.mqr.common.plugin.annotation.PHook;
import com.molicloud.mqr.common.plugin.enums.RobotEventEnum;
import com.molicloud.mqr.common.plugin.message.Ats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 广告/违法信息检测插件
 *
 * 监听所有消息的插件示例
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/9 5:38 下午
 */
@Slf4j
@Component
public class AdblockPluginExecutor implements PluginExecutor {

    public static final String[] adKeywords = new String[]{
            "日赚", "月赚", "招收", "招商"
    };

    private static final String warnContent = "请勿发送广告或违法信息";

    @PHook(name = "Adblock",
            listeningAllMessage = true,
            robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        Object message = pluginParam.getData();
        if (message instanceof String) {
            if (Arrays.stream(adKeywords).anyMatch(keyword -> String.valueOf(message).equalsIgnoreCase(keyword))) {
                pluginResult.setProcessed(true);
                if (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum())) {
                    Ats ats = new Ats();
                    ats.setMids(Arrays.asList(pluginParam.getFrom()));
                    ats.setContent(warnContent);
                    pluginResult.setData(ats);
                } else {
                    pluginResult.setData(warnContent);
                }
            }
        }

        return pluginResult;
    }
}
