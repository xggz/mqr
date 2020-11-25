package com.molicloud.mqr.plugin.aireply;

import com.molicloud.mqr.common.plugin.PluginExecutor;
import com.molicloud.mqr.common.plugin.PluginParam;
import com.molicloud.mqr.common.plugin.PluginResult;
import com.molicloud.mqr.common.plugin.annotation.PHook;
import com.molicloud.mqr.common.plugin.enums.RobotEventEnum;
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

    // 茉莉机器人API，以下api仅供测试，如需自定义词库和机器人名字等，请前往官网获取，获取地址 http://www.itpk.cn
    private static final String apiKey = "2efdd0243d746921c565225ca4fdf07b";
    private static final String apiSecret = "itpk123456";

    @PHook(name = "AiReply",
            defaulted = true,
            robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        String reply = aiReply(String.valueOf(pluginParam.getData()));
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setMessage(reply);
        return pluginResult;
    }

    private String aiReply(String message) {
        String aiUrl = String.format("http://i.itpk.cn/api.php?question=%s&api_key=%s&api_secret=%s", message, apiKey, apiSecret);
        return restTemplate.getForObject(aiUrl, String.class);
    }
}
