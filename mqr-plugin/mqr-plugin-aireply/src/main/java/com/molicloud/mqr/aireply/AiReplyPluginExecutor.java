package com.molicloud.mqr.aireply;

import com.molicloud.mqr.common.PluginExecutor;
import com.molicloud.mqr.common.PluginParam;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.annotation.PHook;
import com.molicloud.mqr.common.enums.RobotEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 智能回复插件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/6 3:45 下午
 */
@Slf4j
@Component
public class AiReplyPluginExecutor implements PluginExecutor {

    @Autowired
    private RestTemplate restTemplate;

    // 茉莉机器人API，获取地址 http://www.itpk.cn
    private static final String apiKey = "2efdd0243d746921c565225ca4fdf07b";
    private static final String apiSecret = "itpk123456";

    @PHook(robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        log.debug("========>>" + pluginParam.toString());
        PluginResult pluginResult = new PluginResult();
        if (RobotEventEnum.FRIEND_MSG.equals(pluginParam.getRobotEventEnum())
                || (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum()) && pluginParam.getTo().equals("273970059"))) {
            pluginResult.setProcessed(true);
            pluginResult.setData(aiReply(String.valueOf(pluginParam.getData())));
        } else {
            pluginResult.setProcessed(false);
        }
        return pluginResult;
    }

    private String aiReply(String message) {
        String aiUrl = String.format("http://i.itpk.cn/api.php?question=%s&api_key=%s&api_secret=%s", message, apiKey, apiSecret);
        return restTemplate.getForObject(aiUrl, String.class);
    }
}
