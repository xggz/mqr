package com.molicloud.mqr.plugin.aireply;

import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.annotation.PJob;
import com.molicloud.mqr.plugin.core.define.RobotDef;
import com.molicloud.mqr.plugin.core.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能回复插件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/6 3:45 下午
 */
@Slf4j
@Component
public class AiReplyPluginExecutor extends AbstractPluginExecutor {

    @Autowired
    private RestTemplate restTemplate;

    // 茉莉机器人API，以下api仅供测试，如需自定义词库和机器人名字等，请前往官网获取，获取地址 http://www.itpk.cn
    private static final String apiKey = "2efdd0243d746921c565225ca4fdf07b";
    private static final String apiSecret = "itpk123456";

    @PHook(name = "AiReply",
            equalsKeywords = { "设置聊天前缀" },
            defaulted = true,
            robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        String message = String.valueOf(pluginParam.getData());
        PluginResult pluginResult = new PluginResult();
        if (ExecuteTriggerEnum.KEYWORD.equals(pluginParam.getExecuteTriggerEnum())) {
            if (Arrays.asList(getAdmins()).contains(pluginParam.getFrom())) {
                pluginResult.setProcessed(true);
                pluginResult.setHold(true);
                pluginResult.setMessage("请在下条消息中告诉我前缀内容");
            } else {
                pluginResult.setProcessed(true);
                pluginResult.setMessage("你没有权限设置聊天前缀");
            }
            return pluginResult;
        } else if (ExecuteTriggerEnum.HOLD.equals(pluginParam.getExecuteTriggerEnum())) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage("聊天前缀已经设置为：".concat(message));
            // 保存配置
            saveHookSetting(message);
            return pluginResult;
        }

        // 获取聊天前缀
        String prefix = getHookSetting(String.class);
        if (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum())
                && StrUtil.isNotEmpty(prefix)
                && !StrUtil.startWith(message, prefix)) {
            pluginResult.setProcessed(false);
        } else {
            String reply = aiReply(message, prefix);
            pluginResult.setProcessed(true);
            pluginResult.setMessage(reply);
        }
        return pluginResult;
    }

    @PJob(cron = "0 0 * * * ?")
    public void handlerTimer() {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        // 获取所有群列表
        List<RobotDef.Group> getGroupList = getGroupList();
        // 整点报时发给所有群
        messageEvent.setToIds(getGroupList.stream().map(RobotDef.Group::getId).collect(Collectors.toList()));
        messageEvent.setMessage("整点报时：" + new Date().toString());
        pushMessage(messageEvent);
    }

    private String aiReply(String message, String prefix) {
        if (StrUtil.isNotEmpty(prefix) && StrUtil.startWith(message, prefix)) {
            message = message.substring(prefix.length());
        }
        String aiUrl = String.format("http://i.itpk.cn/api.php?question=%s&api_key=%s&api_secret=%s", message, apiKey, apiSecret);
        return restTemplate.getForObject(aiUrl, String.class);
    }
}