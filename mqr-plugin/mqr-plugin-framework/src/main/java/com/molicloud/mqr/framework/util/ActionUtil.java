package com.molicloud.mqr.framework.util;

import cn.hutool.core.collection.CollUtil;
import com.molicloud.mqr.plugin.core.action.Action;
import com.molicloud.mqr.plugin.core.action.KickAction;
import com.molicloud.mqr.plugin.core.action.MuteAction;
import com.molicloud.mqr.plugin.core.action.UnmuteAction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件动作工具类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/17 11:38 上午
 */
@Slf4j
@UtilityClass
public class ActionUtil {

    /**
     * 处理群动作
     *
     * @param group
     * @param action
     */
    public void handlerGroupAction(Group group, Action action) {
        if (action != null) {
            List<String> ids = action.getIds();
            ContactList<Member> memberContactList = group.getMembers();
            List<Member> memberList = ids.stream().map(mid -> memberContactList.get(Long.parseLong(mid))).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(memberList)) {
                if (action instanceof MuteAction) {
                    MuteAction muteAction = (MuteAction) action;
                    memberList.stream().forEach(member -> member.mute(muteAction.getSeconds()));
                } else if (action instanceof UnmuteAction) {
                    memberList.stream().forEach(member -> member.unmute());
                } else if (action instanceof KickAction) {
                    memberList.stream().forEach(member -> member.kick());
                }
            }
        }
    }

    /**
     * 处理好友动作
     *
     * @param friend
     * @param action
     */
    public void handlerFriendAction(Friend friend, Action action) {
        if (action != null) {
            log.debug("好友消息不支持此动作");
        }
    }

    /**
     * 处理临时好友动作
     *
     * @param member
     * @param action
     */
    public void handlerMemberAction(Member member, Action action) {
        if (action != null) {
            log.debug("临时好友消息不支持此动作");
        }
    }
}