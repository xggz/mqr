package com.molicloud.mqr.plugin.signin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginInfo;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.define.FaceDef;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.message.MessageBuild;
import com.molicloud.mqr.plugin.core.message.make.Ats;
import com.molicloud.mqr.plugin.core.message.make.Expression;
import com.molicloud.mqr.plugin.core.message.make.Text;
import com.molicloud.mqr.plugin.signin.entity.RobotPluginSignIn;
import com.molicloud.mqr.plugin.signin.mapper.RobotPluginSignInMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 签到插件
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/03 2:44 下午
 */
@Slf4j
@Component
public class SignInPluginExecutor extends AbstractPluginExecutor {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RobotPluginSignInMapper mapper;

    @PHook(name = "SignIn", equalsKeywords = {
            "签到",
    }, robotEvents = {
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {
        PluginResult pluginResult = new PluginResult();
        MessageBuild messageBuild = new MessageBuild();
        pluginResult.setProcessed(true);

        Ats ats = new Ats();
        ats.setMids(Arrays.asList(pluginParam.getFrom()));

        // 判断今天是否有签到
        if (getTodaySignInCount(pluginParam.getTo(), pluginParam.getFrom()) >= 1) {
            ats.setContent("你今天已经签到过啦，明天再来吧～");
            messageBuild.append(ats);
            pluginResult.setMessage(messageBuild);
            return pluginResult;
        }

        // 昨天签到人数
        Integer todaySignInNum = getTodaySignInNum(pluginParam.getTo());
        // 昨天是否签到
        Boolean isYesterdaySignIn = isYesterdaySignIn(pluginParam.getTo(), pluginParam.getFrom());
        // 一言
        String hitokoto = hitokoto();
        String content = "";

        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(new Date());
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 6) {
            content = "早上好，今天又是充满元气的一天喔～";
        }
        if (a > 6 && a <= 12) {
            content = "上午好，今天有多喝水吗？";
        }
        if (a == 13) {
            content = "中午好，午饭要吃的饱饱的，要记得午睡喔～";
        }
        if (a > 13 && a <= 18) {
            content = "下午好，一天已经过去一半啦！";
        }
        if (a > 18 && a <= 24) {
            content = "晚上好，早点休息鸭～";
        }

        content += String.format("\r\n签到成功～，今天你是第 %d 个签到的哟～", todaySignInNum + 1);
        ats.setContent(content);
        messageBuild.append(ats);
        messageBuild.append(new Expression(FaceDef.meigui));

        int num = 1; // 连续签到次数
        // 判断是否是连续签到
        if (isYesterdaySignIn) {
            // 获取最近的一次签到
            List<RobotPluginSignIn> list = mapper.selectList(
                    Wrappers.<RobotPluginSignIn>lambdaQuery().orderByDesc(RobotPluginSignIn::getId)
                            .eq(RobotPluginSignIn::getGroupId, pluginParam.getTo())
                            .eq(RobotPluginSignIn::getQq, pluginParam.getFrom())
            );
            RobotPluginSignIn newestSignIn = list.get(0);
            num = newestSignIn.getNum() + 1;
            messageBuild.append(new Text(String.format("\r\n截止今日，你已经连续签到 %d 天啦！明天还要继续加油鸭～", num)));
        }

        messageBuild.append(new Text(String.format("\r\n今日份鸡汤「%s」", hitokoto)));

        pluginResult.setMessage(messageBuild);
        RobotPluginSignIn signInLog = new RobotPluginSignIn();
        signInLog.setQq(pluginParam.getFrom());
        signInLog.setGroupId(pluginParam.getTo());
        signInLog.setIsContinuity(num > 1);
        signInLog.setMotto(hitokoto);
        signInLog.setNum(num);
        mapper.insert(signInLog);

        return pluginResult;
    }

    /**
     * 判断昨天有没有签到
     *
     * @param groupId 群号
     * @param qq      消息来源QQ
     * @return
     */
    private Boolean isYesterdaySignIn(String groupId, String qq) {
        return mapper.selectCount(Wrappers.<RobotPluginSignIn>lambdaQuery()
                .eq(RobotPluginSignIn::getGroupId, groupId)
                .eq(RobotPluginSignIn::getQq, qq)
                .likeRight(
                        RobotPluginSignIn::getCreateTime,
                        new SimpleDateFormat("yyyy-MM-dd").format(
                                new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
                        )
                )
        ) >= 1;
    }

    /**
     * 获取今日签到次数
     *
     * @param groupId 群号
     * @param qq      消息来源QQ
     * @return
     */
    private Integer getTodaySignInCount(String groupId, String qq) {
        return mapper.selectCount(Wrappers.<RobotPluginSignIn>lambdaQuery()
                .eq(RobotPluginSignIn::getGroupId, groupId)
                .eq(RobotPluginSignIn::getQq, qq)
                .likeRight(RobotPluginSignIn::getCreateTime, new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
        );
    }

    /**
     * 获取今日签到人数
     *
     * @param groupId 群号
     * @return
     */
    private Integer getTodaySignInNum(String groupId) {
        return mapper.selectCount(Wrappers.<RobotPluginSignIn>lambdaQuery()
                .eq(RobotPluginSignIn::getGroupId, groupId)
                .likeRight(RobotPluginSignIn::getCreateTime, new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
        );
    }

    /**
     * 获取全部签到次数
     *
     * @param groupId 群号
     * @param qq      消息来源QQ
     * @return
     */
    private Integer getSignInCount(String groupId, String qq) {
        return mapper.selectCount(Wrappers.<RobotPluginSignIn>lambdaQuery()
                .eq(RobotPluginSignIn::getGroupId, groupId)
                .eq(RobotPluginSignIn::getQq, qq)
        );
    }

    /**
     * 一言
     *
     * @return
     */
    private String hitokoto() {
        String content = "";
        try {
            content = restTemplate.getForObject("https://v1.hitokoto.cn/?c=d&encode=text", String.class);
        } catch (RestClientException e) {
            content = "哎呀，今日没有鸡汤~";
        }
        return content;
    }

    @Override
    public PluginInfo getPluginInfo() {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setName("签到插件");
        pluginInfo.setAuthor("WispX");
        pluginInfo.setHomeUrl("https://github.com/wisp-x");
        pluginInfo.setExplain("群签到插件，支持连续签到、签到后发送一言");
        pluginInfo.setVersion(10001);
        pluginInfo.setInitScript(
                "CREATE TABLE \"robot_plugin_signin\" (" +
                        "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "  \"group_id\" VARCHAR(50) NOT NULL," +
                        "  \"qq\" VARCHAR(50) NOT NULL," +
                        "  \"is_continuity\" BIT(1) NOT NULL DEFAULT 0," +
                        "  \"num\" INTEGER NOT NULL DEFAULT 0," +
                        "  \"motto\" TEXT NOT NULL," +
                        "  \"update_time\" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "  \"create_time\" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                        ");");
        return pluginInfo;
    }
}
