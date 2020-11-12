package com.molicloud.mqr.joke;

import cn.hutool.json.JSONObject;
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
 * 笑话插件
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/11/12 1:30 下午
 */
@Slf4j
@Component
public class JokePluginExecutor implements PluginExecutor {

    @Autowired
    private RestTemplate restTemplate;

    @PHook(name = "Joke", keywords = {
            "笑话", "讲个笑话", "说个笑话"
    }, robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setData(getJoke());
        return pluginResult;
    }

    public String getJoke() {
        String url = "http://i.itpk.cn/api.php?question=笑话";
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(response);
        return String.format("《%s》\r\n\r\n%s", jsonObject.getStr("title"), jsonObject.getStr("content"));
    }
}
