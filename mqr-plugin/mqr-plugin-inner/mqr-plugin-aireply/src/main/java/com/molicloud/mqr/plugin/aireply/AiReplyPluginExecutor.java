package com.molicloud.mqr.plugin.aireply;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.annotation.PJob;
import com.molicloud.mqr.plugin.core.define.RobotDef;
import com.molicloud.mqr.plugin.core.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import com.molicloud.mqr.plugin.core.message.make.Img;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.LocalTime;
import java.util.Arrays;
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

    @PHook(name = "AiReply",
            equalsKeywords = { "设置聊天前缀", "取消聊天前缀", "开启@回复", "打开@回复", "关闭@回复", "取消@回复", "设置报时类型", "设置报时者名字", "设置聊天api", "设置聊天Api", "设置聊天API" },
            defaulted = true,
            robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        // 接收消息
        String message = String.valueOf(pluginParam.getData());
        // 获取配置
        AiRepltSetting aiRepltSetting = getHookSetting(AiRepltSetting.class);
        if (aiRepltSetting == null) {
            aiRepltSetting = new AiRepltSetting();
        }
        // 实例化回复对象
        PluginResult pluginResult = new PluginResult();
        if (ExecuteTriggerEnum.KEYWORD.equals(pluginParam.getExecuteTriggerEnum())) {
            if (Arrays.asList(getAdmins()).contains(pluginParam.getFrom())) {
                if ("设置聊天前缀".equals(pluginParam.getKeyword())) {
                    pluginResult.setProcessed(true);
                    pluginResult.setHold(true);
                    pluginResult.setMessage("请在下条消息中告诉我前缀内容");
                } else if ("取消聊天前缀".equals(pluginParam.getKeyword())) {
                    // 保存配置
                    aiRepltSetting.setPrefix("");
                    saveHookSetting(aiRepltSetting);
                    pluginResult.setProcessed(true);
                    pluginResult.setMessage("聊天前缀取消成功");
                } else if ("开启@回复".equals(pluginParam.getKeyword()) || "打开@回复".equals(pluginParam.getKeyword())) {
                    // 保存配置
                    aiRepltSetting.setAtReply(true);
                    saveHookSetting(aiRepltSetting);
                    pluginResult.setProcessed(true);
                    pluginResult.setMessage("已开启@回复");
                } else if ("关闭@回复".equals(pluginParam.getKeyword()) || "取消@回复".equals(pluginParam.getKeyword())) {
                    // 保存配置
                    aiRepltSetting.setAtReply(false);
                    saveHookSetting(aiRepltSetting);
                    pluginResult.setProcessed(true);
                    pluginResult.setMessage("已关闭@回复");
                } else if ("设置报时类型".equals(pluginParam.getKeyword())) {
                    pluginResult.setProcessed(true);
                    pluginResult.setHold(true);
                    pluginResult.setMessage("请回复编号（1：所有群，2：仅白名单内的群，3：关闭报时）");
                } else if ("设置报时者名字".equals(pluginParam.getKeyword())) {
                    pluginResult.setProcessed(true);
                    pluginResult.setHold(true);
                    pluginResult.setMessage("请在下条消息中告诉我报时者名字");
                } else if ("设置聊天Api".equalsIgnoreCase(pluginParam.getKeyword())) {
                    pluginResult.setProcessed(true);
                    pluginResult.setHold(true);
                    pluginResult.setMessage("请在下条消息中告诉我API信息！\r\n示例（前面ApiKey，后面ApiSecret，中间一个逗号）：\r\nc448e40247027896,ttttt888");
                }
            } else {
                pluginResult.setProcessed(true);
                pluginResult.setMessage("你没有权限操作");
            }
            return pluginResult;
        } else if (ExecuteTriggerEnum.HOLD.equals(pluginParam.getExecuteTriggerEnum())) {
            pluginResult.setProcessed(true);
            if ("设置聊天前缀".equals(pluginParam.getHoldMessage())) {
                aiRepltSetting.setPrefix(message);
                pluginResult.setMessage("聊天前缀已经设置为：".concat(message));
            } else if ("设置报时类型".equals(pluginParam.getHoldMessage())) {
                if (message.equals("1")) {
                    aiRepltSetting.setTimerType(1);
                    pluginResult.setMessage("已修改为给所有群报时");
                } else if (message.equals("2")) {
                    aiRepltSetting.setTimerType(2);
                    pluginResult.setMessage("已修改为仅给白名单内的群报时");
                } else if (message.equals("3")) {
                    aiRepltSetting.setTimerType(3);
                    pluginResult.setMessage("已关闭群报时功能");
                } else {
                    pluginResult.setMessage("设置无效");
                }
            } else if ("设置报时者名字".equals(pluginParam.getHoldMessage())) {
                aiRepltSetting.setTimerName(message);
                pluginResult.setMessage("报时者名字已经设置为：".concat(message));
            } else if ("设置聊天Api".equalsIgnoreCase(pluginParam.getHoldMessage())) {
                if ("退出".equals(message)) {
                    pluginResult.setMessage("已退出聊天API设置");
                    return pluginResult;
                }
                String[] apiInfo = getApiInfo(message);
                if (apiInfo == null || apiInfo.length != 2) {
                    pluginResult.setHold(true);
                    pluginResult.setMessage("聊天API信息格式不正确！\n继续设置示例（前面ApiKey，后面ApiSecret，中间一个逗号）：\r\nc448e40247027896,ttttt888\r\n退出设置请回复：退出");
                    return pluginResult;
                }
                aiRepltSetting.setApiKey(apiInfo[0]);
                aiRepltSetting.setApiSecret(apiInfo[1]);
                pluginResult.setMessage("聊天API设置成功！");
            }
            // 保存配置
            saveHookSetting(aiRepltSetting);
            return pluginResult;
        }

        // 判断聊天Api是否已设置
        if (StrUtil.isBlank(aiRepltSetting.getApiKey()) || StrUtil.isBlank(aiRepltSetting.getApiSecret())) {
            pluginResult.setProcessed(true);
            pluginResult.setMessage("请管理员设置聊天Api来开启闲聊功能（私聊机器人以下指令）：\r\n设置聊天Api");
            return pluginResult;
        }

        // 是否机器人被At才回复
        Boolean atReply = aiRepltSetting.getAtReply();
        if (atReply != null && atReply && !pluginParam.isAt()) {
            return pluginResult;
        }
        // 获取聊天前缀
        String prefix = aiRepltSetting.getPrefix();
        if (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum())
                && StrUtil.isNotEmpty(prefix)
                && !StrUtil.startWith(message, prefix)) {
            pluginResult.setProcessed(false);
        } else {
            pluginResult.setProcessed(true);
            JSONObject reply = aiReply(pluginParam, aiRepltSetting);
            if (!"00000".equals(reply.getStr("code"))) {
                pluginResult.setMessage(reply.getStr("message").concat(" 请重新设置聊天API或联系管理员"));
            } else {
                JSONArray dataArray = reply.getJSONArray("data");
                for (int i = 0; i < dataArray.size() - 1; i++) {
                    JSONObject data = (JSONObject) dataArray.get(i);
                    Object info = convertMessageData(data);
                    if (info != null) {
                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.setRobotEventEnum(pluginParam.getRobotEventEnum());
                        messageEvent.setToIds(Arrays.asList(pluginParam.getTo()));
                        messageEvent.setMessage(info);
                        pushMessage(messageEvent);
                    }
                }

                // 最后一条消息返回发送
                JSONObject lastData = (JSONObject) dataArray.get(dataArray.size()-1);
                pluginResult.setMessage(convertMessageData(lastData));
            }
        }
        return pluginResult;
    }

    @PJob(cron = "0 0 * * * ?", hookName = "AiReply")
    public void handlerTimer() {
        // 获取配置
        AiRepltSetting aiRepltSetting = getHookSetting(AiRepltSetting.class);
        if (aiRepltSetting == null
                || aiRepltSetting.getTimerType() == null
                || aiRepltSetting.getTimerType() == 3) {
            return;
        }
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        if (aiRepltSetting == null
                || aiRepltSetting.getTimerType() == null
                || aiRepltSetting.getTimerType() == 1) {
            // 获取所有群列表
            List<RobotDef.Group> getGroupList = getGroupList();
            // 整点报时发给所有群
            messageEvent.setToIds(getGroupList.stream().map(RobotDef.Group::getId).collect(Collectors.toList()));
        } else {
            // 获取所有的白名单群ID列表
            List<String> groupIdList = getGroupIdAllowList();
            // 整点报时发给所有群
            messageEvent.setToIds(groupIdList);
        }
        if (CollUtil.isNotEmpty(messageEvent.getToIds())) {
            Integer hour = LocalTime.now().getHour();
            String name = aiRepltSetting == null || StrUtil.isBlank(aiRepltSetting.getTimerName()) ? "茉莉" : aiRepltSetting.getTimerName();
            messageEvent.setMessage(getTipByHour(hour, name));
            pushMessage(messageEvent);
        }
    }

    private JSONObject aiReply(PluginParam pluginParam, AiRepltSetting aiRepltSetting) {
        String prefix = aiRepltSetting.getPrefix();
        String message = String.valueOf(pluginParam.getData());
        if (StrUtil.isNotEmpty(prefix) && StrUtil.startWith(message, prefix)) {
            message = message.substring(prefix.length());
        }
        message = message.replaceAll("(\\[mirai:.+?\\])", "");
        message = message.replaceAll("(@[0-9]+?\\s)", "");

        return aichat(message, aiRepltSetting, pluginParam);
    }

    private JSONObject aichat(String message, AiRepltSetting aiRepltSetting, PluginParam pluginParam) {
        int type = pluginParam.getRobotEventEnum().equals(RobotEventEnum.GROUP_MSG) ? 2 : 1;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Api-Key", aiRepltSetting.getApiKey());
        headers.add("Api-Secret", aiRepltSetting.getApiSecret());

        JSONObject body = new JSONObject();
        body.set("content", message);
        body.set("type", type);
        body.set("from", pluginParam.getFrom());
        body.set("fromName", pluginParam.getFromName());
        body.set("to", pluginParam.getTo());
        body.set("toName", pluginParam.getToName());

        HttpEntity<String> formEntity = new HttpEntity<String>(body.toString(), headers);
        return restTemplate.postForEntity("http://openapi.itpk.cn/reply", formEntity, JSONObject.class).getBody();
    }

    /**
     * 转换消息数据
     *
     * @param data
     * @return
     */
    private Object convertMessageData(JSONObject data) {
        Integer typed = data.getInt("typed");
        if (OpenApiMessageTyped.TEXT.getValue().equals(typed)) {
            return data.getStr("content");
        } else if (OpenApiMessageTyped.IMAGE.getValue().equals(typed)) {
            try {
                URL url = new URL("https://files.molicloud.com/".concat(data.getStr("content")));
                return new Img(url.openStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            return data.getStr("remark");
        }
        return null;
    }

    /**
     * 从消息中获取Api信息
     *
     * @param message
     * @return
     */
    private String[] getApiInfo(String message) {
        if (message.indexOf(",") > 0) {
            return message.split(",");
        } else if (message.indexOf("，") > 0) {
            return message.split("，");
        } else if (message.indexOf(" ") > 0) {
            return message.split(" ");
        } else if (message.indexOf("\r\n") > 0) {
            return message.split("\r\n");
        } else if (message.indexOf("\n") > 0) {
            return message.split("\n");
        }
        return null;
    }

    /**
     * 根据当前小时获取提示语
     *
     * @param hour
     * @return
     */
    private String getTipByHour(Integer hour, String name) {
        String tip = "";
        switch (hour) {
            case 0:
                tip = "穿过挪威的森林，让我走进你梦里，夕阳落在我的铠甲，王子不一定骑白马，黑马王子四海为家。\r\n我是" + name + "，现在是凌晨十二点。";
                break;
            case 1:
                tip = "凌晨一点了，你还瞪起眼睛像铜铃，如果实在睡不着，那就来找我聊天试试。";
                break;
            case 2:
                tip = "凌晨两点了你咋个还没睡哦，这个世界上有很多爱你的人，所以你也要好好爱你个人哦。";
                break;
            case 3:
                tip = "所有在梦想面前受的伤都可以忽略不计，如果我做得到，那你也可以，现在凌晨三点钟。";
                break;
            case 4:
                tip = "上帝关上门肯定会为你开窗，记得有我给你们加油打气，我是" + name + "，现在是凌晨四点整。";
                break;
            case 5:
                tip = "你的人生目标找到没，是不是还觉得世界不公平，不要害怕，有一天你也会闪闪发光，现在是清晨5点。";
                break;
            case 6:
                tip = "我们用爱和希望来给世界做点缀，走在沙漠也会像在花丛中一样，现在是早上六点。";
                break;
            case 7:
                tip = "现在是早上7点，" + name + "问候你早安。\r\n早餐是大脑活动的能量之源，请勿空腹开启一天的工作，早餐您吃了吗？";
                break;
            case 8:
                tip = "天生我才那就必定有用，只要你还有梦，总有一天墙角也会盛开花，我是" + name + "，现在是早上八点，开启新的一天，掌声送给还在奋斗中的你。";
                break;
            case 9:
                tip = "永远相信自己不一般，不管他们喜不喜欢，所以坚持你的道路勇往直前，现在是九点整，我是" + name;
                break;
            case 10:
                tip = "我是" + name + "，现在是上午十点，我晓得你在想撒子，遇到困难不要怕，继续加把劲，努力向梦想顶端前进。";
                break;
            case 11:
                tip = "现在是上午11点，工作半天了，" + name + "邀请您从电脑上移开视线，站起来伸个懒腰，看看窗外，快乐工作，快乐生活。";
                break;
            case 12:
                tip = "现在是午间12点，享受午餐，享受放松时光，" + name + "祝你午餐好胃口。";
                break;
            case 13:
                tip = "现在是午后1点，" + name + "提醒您休息片刻，下午的工作更有精神。";
                break;
            case 14:
                tip = "现在是午后2点，开始下午的工作哦，" + name + "与你一起努力。";
                break;
            case 15:
                tip = "青蛙都有机会变成王子，你也可以变得出类拔萃，现在是下午三点。";
                break;
            case 16:
                tip = "不是每个人生来就是白马王子，但是这有啥关系喃，巴巴掌送给正在努力中的你，我是" + name + "，现在是下午四点。";
                break;
            case 17:
                tip = "我们都是乖娃娃，现在是下午五点，你还不饿嗦，搞快去吃晚饭唠。";
                break;
            case 18:
                tip = "现在是下午6点，" + name + "提示正在回家路上的你，注意安全，安全是亲人的期待。";
                break;
            case 19:
                tip = "现在是晚上7点，" + name + "祝您晚餐好胃口，陪伴家人，享受亲情，幸福其实很简单。";
                break;
            case 20:
                tip = "现在是晚上8点，无论是家人在一起，还是朋友在一起，" + name + "倡议放下手机，让我们的情感回归本源。";
                break;
            case 21:
                tip = "现在是晚间9点，灯火璀璨，城市在夜色中绽放，" + name + "与你共享美丽夜色。";
                break;
            case 22:
                tip = "现在是晚间10点，喧嚣退去，城市开始安静的呼吸。\r\n" + name + "提醒你早睡早起，有益身体的健康。";
                break;
            case 23:
                tip = "现在是晚间11点，夜色深沉，繁星点点下的美丽城市，" + name + "与您走向梦的香甜！";
                break;
            default:
                tip = "";
                break;
        }
        return tip;
    }
}