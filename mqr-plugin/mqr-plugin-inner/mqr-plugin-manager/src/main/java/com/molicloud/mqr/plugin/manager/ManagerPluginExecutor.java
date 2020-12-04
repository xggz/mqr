package com.molicloud.mqr.plugin.manager;

import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.action.KickAction;
import com.molicloud.mqr.plugin.core.action.MuteAction;
import com.molicloud.mqr.plugin.core.action.UnmuteAction;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.define.AtDef;
import com.molicloud.mqr.plugin.core.define.FaceDef;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.message.MessageBuild;
import com.molicloud.mqr.plugin.core.message.make.Ats;
import com.molicloud.mqr.plugin.core.message.make.Expression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 机器人管理插件 TODO 待优化
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/11/28 9:11 上午
 */
@Slf4j
@Component
public class ManagerPluginExecutor extends AbstractPluginExecutor {

    /**
     * 被操作对象QQ列表
     */
    private List<String> ids = new LinkedList<>();

    /**
     * 指令列表
     */
    private String[] commands = {"禁言", "解禁", "踢人"};

    @PHook(name = "Manager", startsKeywords = {
            "禁言", "解禁", "踢人"
    }, robotEvents = {
            RobotEventEnum.FRIEND_MSG,
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        if (RobotEventEnum.GROUP_MSG.equals(pluginParam.getRobotEventEnum())) {
            // 判断消息发送者是否为管理员
            if (!Arrays.asList(getAdmins()).contains(pluginParam.getFrom())) {
                MessageBuild messageBuild = new MessageBuild();
                Ats ats = new Ats();
                ats.setMids(Arrays.asList(pluginParam.getFrom()));
                ats.setContent("您没有权限执行该操作");
                messageBuild.append(ats);
                messageBuild.append(new Expression(FaceDef.zuohengheng));
                pluginResult.setMessage(messageBuild);
                return pluginResult;
            }
            String message = String.valueOf(pluginParam.getData());
            String command = getCommand(message);
            List<AtDef> atDefs = pluginParam.getAts();
            ids = atDefs.stream().map(AtDef::getId).distinct().collect(Collectors.toList());
            if (ids.isEmpty()) {
                pluginResult.setMessage("未选择操作对象");
                return pluginResult;
            }
            switch (command) {
                case "禁言":
                    return mute(pluginResult, getArgsContent(atDefs, message));
                case "解禁":
                    pluginResult.setAction(new UnmuteAction(ids));
                    break;
                case "踢人":
                    pluginResult.setAction(new KickAction(ids));
                    break;
            }
            pluginResult.setMessage("操作成功");
        } else {
        }

        return pluginResult;
    }

    /**
     * 禁言用户
     *
     * @param pluginResult
     * @param args
     * @return
     */
    private PluginResult mute(PluginResult pluginResult, String args) {
        if (args.isEmpty()) {
            pluginResult.setMessage("指令错误");
            return pluginResult;
        }
        log.debug(args);
        Integer seconds;
        String arg = args.replaceAll("[^\\u4e00-\\u9fa5]", "");
        String value = getArgNum(args);
        log.debug(arg);
        log.debug(value);
        if (value.isEmpty() || !isInteger(value)) {
            pluginResult.setMessage("禁言时间错误");
            return pluginResult;
        }
        Integer val = Integer.valueOf(value);
        switch (arg) {
            case "秒":
                seconds = val;
                break;
            case "分钟":
                seconds = val * 60;
                break;
            case "小时":
                seconds = val * 3600;
                break;
            case "天":
                seconds = val * 86400;
                break;
            case "月":
            case "个月":
                seconds = 86400 * 30;
                break;
            default:
                pluginResult.setMessage("指令错误");
                return pluginResult;
        }

        if (seconds > 2592000) {
            pluginResult.setMessage("禁言最长时间为 30 天");
            return pluginResult;
        }

        pluginResult.setAction(new MuteAction(ids, seconds));
        pluginResult.setMessage("操作成功");
        return pluginResult;
    }

    /**
     * 获取指令内容
     *
     * @param atDefs
     * @param message
     * @return
     */
    private String getArgsContent(List<AtDef> atDefs, String message) {
        String content = message.substring(2).trim(); // 指令后面的内容
        for (AtDef atDef : atDefs) content = content.replaceAll(atDef.getNick(), "");
        return content.trim();
    }

    /**
     * 获取消息指令
     *
     * @param message
     * @return
     */
    private String getCommand(String message) {
        for (String command : commands) {
            if (command != null && StrUtil.startWith(message, command)) {
                return command;
            }
        }
        return "";
    }

    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private static String getArgNum(String str) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
