package com.molicloud.mqr.plugin.votekick;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginInfo;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.action.KickAction;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.define.AtDef;
import com.molicloud.mqr.plugin.core.define.FaceDef;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.event.MessageEvent;
import com.molicloud.mqr.plugin.core.message.MessageBuild;
import com.molicloud.mqr.plugin.core.message.make.Ats;
import com.molicloud.mqr.plugin.core.message.make.Expression;
import com.molicloud.mqr.plugin.core.message.make.Text;
import com.molicloud.mqr.plugin.votekick.entity.RobotPluginVoteKick;
import com.molicloud.mqr.plugin.votekick.mapper.RobotPluginManagerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 投票踢人插件
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/04 2:55 下午
 */
@Slf4j
@Component
public class VoteKickPluginExecutor extends AbstractPluginExecutor {

    @Autowired
    private RobotPluginManagerMapper mapper;

    private MessageEvent messageEvent = new MessageEvent();

    /**
     * 最大票决次数, TODO 按群设置
     */
    private final Integer maxNum = 10;

    @PHook(name = "VoteKick",
            startsKeywords = {
            "投票踢人", "清空投票", "清空投票次数", "查询投票次数", "查看投票次数", "投票次数", "清空全部投票",
            "清空所有投票",
    }, robotEvents = {
            RobotEventEnum.GROUP_MSG,
    })
    public PluginResult messageHandler(PluginParam pluginParam) {

        PluginResult pluginResult = new PluginResult();
        MessageBuild messageBuild = new MessageBuild();
        pluginResult.setProcessed(true);
        messageEvent.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        messageEvent.setToIds(Arrays.asList(pluginParam.getTo()));

        try {
            if (!pluginParam.getKeyword().equals("投票踢人") && !Arrays.asList(getAdmins()).contains(pluginParam.getFrom())) {
                Ats ats = new Ats();
                ats.setMids(Arrays.asList(pluginParam.getFrom()));
                messageBuild.append(ats);
                messageBuild.append(new Expression(FaceDef.zuohengheng));
                throw new Exception("您没有权限执行该操作");
            }

            List<AtDef> atDefs = pluginParam.getAts();
            if (!pluginParam.getKeyword().equals("清空全部投票") && atDefs.size() == 0) {
                throw new Exception("请选择「@」群成员");
            }
            switch (pluginParam.getKeyword()) {
                case "投票踢人":
                    if (atDefs.size() > 1) {
                        throw new Exception("一次只能投票一人");
                    }
                    if (pluginParam.isAt()) {
                        throw new Exception("我踢我自己？");
                    }

                    AtDef to = ((AtDef) pluginParam.getAts().get(0)); // 被投票人
                    // 判断投票人是否已经投过票
                    if (mapper.selectCount(Wrappers.<RobotPluginVoteKick>lambdaQuery()
                            .eq(RobotPluginVoteKick::getFromQq, pluginParam.getFrom())
                            .eq(RobotPluginVoteKick::getGroupId, pluginParam.getTo())
                            .eq(RobotPluginVoteKick::getQq, to.getId()))
                            >= 1) {
                        Ats ats = new Ats();
                        ats.setMids(Arrays.asList(pluginParam.getFrom()));
                        messageBuild.append(ats);
                        throw new Exception("你已经投过票了！");
                    }

                    int num = vote(pluginParam);
                    if (num >= maxNum) {
                        Ats ats = new Ats();
                        ats.setMids(Arrays.asList(to.getId()));
                        ats.setContent("票数已达上限，10 秒后你将会被移出本群");
                        messageBuild.append(ats);
                        messageEvent.setMessage(messageBuild);
                        Thread thread = new Thread(() -> {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pluginResult.setAction(new KickAction(Arrays.asList(to.getId())));
                            // 删除该用户投票记录
                            mapper.delete(Wrappers.<RobotPluginVoteKick>lambdaQuery()
                                    .eq(RobotPluginVoteKick::getGroupId, pluginParam.getTo())
                                    .eq(RobotPluginVoteKick::getQq, to.getId()));
                        });
                        thread.setDaemon(true);
                        thread.setName("投票踢人线程");
                        thread.start();
                    }
                    break;
                case "清空投票":
                case "清空投票次数":
                    for (AtDef atDef : atDefs) {
                        mapper.delete(Wrappers.<RobotPluginVoteKick>lambdaQuery()
                                .eq(RobotPluginVoteKick::getGroupId, pluginParam.getTo())
                                .eq(RobotPluginVoteKick::getQq, atDef.getId()));
                    }
                    messageBuild.append(new Text("清空成功"));
                    break;
                case "查询投票次数":
                case "查看投票次数":
                case "投票次数":
                    int count = mapper.selectCount(
                            Wrappers.<RobotPluginVoteKick>lambdaQuery().orderByDesc(RobotPluginVoteKick::getId)
                                    .eq(RobotPluginVoteKick::getGroupId, pluginParam.getTo())
                                    .eq(RobotPluginVoteKick::getQq, atDefs.get(0).getId())
                    );
                    messageBuild.append(new Text("该成员票数为 " + count));
                    break;
                case "清空全部投票":
                case "清空所有投票":
                    mapper.delete(Wrappers.<RobotPluginVoteKick>lambdaQuery()
                            .eq(RobotPluginVoteKick::getGroupId, pluginParam.getTo()));
                    messageBuild.append(new Text("清空成功"));
                    break;
                default:
                    throw new Exception("错误指令");
            }
        } catch (Exception e) {
            messageBuild.append(new Text(e.getMessage()));
        }

        if (messageBuild.getMakes().size() > 0) {
            messageEvent.setMessage(messageBuild);
            pushMessage(messageEvent);
        }

        return pluginResult;
    }

    /**
     * 获取被投票人得到的票数
     *
     * @param pluginParam
     * @return
     */
    private Integer getVoteNum(PluginParam pluginParam) {
        return mapper.selectCount(Wrappers.<RobotPluginVoteKick>lambdaQuery()
                .eq(RobotPluginVoteKick::getGroupId, pluginParam.getTo())
                .eq(RobotPluginVoteKick::getQq, ((AtDef) pluginParam.getAts().get(0)).getId())
        );
    }

    /**
     * 发起投票，返回被投票人获得的总票数
     *
     * @return
     */
    private Integer vote(PluginParam pluginParam) {
        MessageBuild messageBuild = new MessageBuild();
        Integer voteNum = getVoteNum(pluginParam);
        AtDef to = (AtDef) pluginParam.getAts().get(0); // 被投票人
        Ats atFrom = new Ats();
        atFrom.setMids(Arrays.asList(pluginParam.getFrom())); // 投票人
        Ats atTo = new Ats();
        atTo.setMids(Arrays.asList(to.getId()));
        if (voteNum == 0) {
            messageBuild
                    .append(new Text("用户"))
                    .append(atFrom)
                    .append(new Text("发起踢人投票，被投票用户"))
                    .append(atTo);
        } else {
            messageBuild
                    .append(atFrom)
                    .append(new Text("投票成功！被投票用户"))
                    .append(atTo);
        }
        messageBuild.append(new Text(String.format("\r\n当前投票次数 %d/%d", voteNum + 1, maxNum)));

        // 写入投票记录
        RobotPluginVoteKick voteKick = new RobotPluginVoteKick();
        voteKick.setGroupId(pluginParam.getTo());
        voteKick.setQq(to.getId());
        voteKick.setFromQq(pluginParam.getFrom());
        mapper.insert(voteKick);

        messageEvent.setMessage(messageBuild);
        pushMessage(messageEvent);

        return voteNum + 1;
    }

    @Override
    public PluginInfo getPluginInfo() {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setName("投票踢人插件");
        pluginInfo.setAuthor("WispX");
        pluginInfo.setHomeUrl("https://github.com/wisp-x");
        pluginInfo.setExplain("群任何成员可发起投票踢人，满票后会被票决出群");
        pluginInfo.setVersion(10001);
        pluginInfo.setInitScript(
                "CREATE TABLE \"robot_plugin_votekick\" (\n" +
                        "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "  \"group_id\" VARCHAR(50) DEFAULT 0,\n" +
                        "  \"from_qq\" VARCHAR(50),\n" +
                        "  \"qq\" VARCHAR(50) DEFAULT 0,\n" +
                        "  \"create_time\" DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  \"update_time\" DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                        ")"
        );
        return pluginInfo;
    }
}