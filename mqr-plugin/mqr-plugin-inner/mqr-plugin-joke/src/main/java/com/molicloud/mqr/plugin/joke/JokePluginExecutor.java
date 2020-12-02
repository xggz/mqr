package com.molicloud.mqr.plugin.joke;

import cn.hutool.json.JSONObject;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 笑话插件
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/11/12 1:30 下午
 */
@Slf4j
@Component
public class JokePluginExecutor extends AbstractPluginExecutor {

    @Autowired
    private RestTemplate restTemplate;

    @PHook(name = "Joke", containsKeywords = {
            "笑话", "搞笑", "段子"
    }, robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setMessage(getJoke());
        return pluginResult;
    }

    public String getJoke() {
        String url = "http://i.itpk.cn/api.php?question=笑话";
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(response);
        return String.format("《%s》\r\n\r\n%s", jsonObject.getStr("title"), jsonObject.getStr("content"));
    }
}
