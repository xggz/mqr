package com.molicloud.mqr.divination;

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
 * 求签插件
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/11/12 4:00 下午
 */
@Slf4j
@Component
public class DivinationPluginExecutor implements PluginExecutor {

    @Autowired
    private RestTemplate restTemplate;

    @PHook(name = "Divination", keywords = {
            "观音灵签", "月老灵签", "财神爷灵签"
    }, robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        String message = String.valueOf(pluginParam.getData());
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setData(getResult(message));
        return pluginResult;
    }

    public String getResult(String message) {
        String url = "http://i.itpk.cn/api.php?question=" + message;
        String response = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = new JSONObject(response);

        String reply = "";
        switch (message) {
            case "观音灵签":
                reply = String.format(
                        "第%s签  %s\r\n\r\n签语：%s\r\n解释：%s\r\n解签：%s",
                        jsonObject.getStr("number1"),
                        jsonObject.getStr("haohua"),
                        jsonObject.getStr("qianyu"),
                        jsonObject.getStr("shiyi"),
                        jsonObject.getStr("jieqian")
                );
                break;
            case "月老灵签":
                reply = String.format(
                        "第%s签  %s\r\n\r\n解释：%s\r\n解签：%s\r\n白话：%s",
                        jsonObject.getStr("number1"),
                        jsonObject.getStr("haohua"),
                        jsonObject.getStr("shiyi"),
                        jsonObject.getStr("jieqian"),
                        jsonObject.getStr("baihua")
                );
                break;
            case "财神爷灵签":
                reply = String.format(
                        "第%s签\r\n签语：%s\r\n注释：%s\r\n解签：%s\r\n解说：%s\r\n结果：%s\r\n婚姻：%s\r\n" +
                                "事业：%s\r\n功名：%s\r\n失物：%s\r\n出外移居：%s\r\n六甲：%s\r\n求财：%s\r\n交易：%s\r\n" +
                                "疾病：%s\r\n诉讼：%s\r\n运途：%s\r\n某事：%s\r\n合伙做生意：%s\r\n",
                        jsonObject.getStr("number1"),
                        jsonObject.getStr("qianyu"),
                        jsonObject.getStr("zhushi"),
                        jsonObject.getStr("jieqian"),
                        jsonObject.getStr("jieshuo"),
                        jsonObject.getStr("jieguo"),
                        jsonObject.getStr("hunyin"),
                        jsonObject.getStr("shiye"),
                        jsonObject.getStr("gongming"),
                        jsonObject.getStr("shiwu"),
                        jsonObject.getStr("cwyj"),
                        jsonObject.getStr("liujia"),
                        jsonObject.getStr("qiucai"),
                        jsonObject.getStr("jiaoyi"),
                        jsonObject.getStr("jibin"),
                        jsonObject.getStr("susong"),
                        jsonObject.getStr("yuntu"),
                        jsonObject.getStr("moushi"),
                        jsonObject.getStr("hhzsy")
                );
                break;
        }

        return reply;
    }
}
