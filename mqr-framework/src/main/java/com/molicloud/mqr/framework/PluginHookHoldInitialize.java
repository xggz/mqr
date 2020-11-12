package com.molicloud.mqr.framework;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.molicloud.mqr.entity.RobotFriend;
import com.molicloud.mqr.entity.RobotGroupMember;
import com.molicloud.mqr.framework.util.PluginHookUtil;
import com.molicloud.mqr.service.RobotFriendService;
import com.molicloud.mqr.service.RobotGroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 初始化机器人好友和群成员持有的插件钩子
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 6:23 下午
 */
@Slf4j
@Component
public class PluginHookHoldInitialize implements CommandLineRunner, Ordered {

    @Autowired
    private RobotFriendService robotFriendService;

    @Autowired
    private RobotGroupMemberService robotGroupMemberService;

    @Override
    public void run(String... args) throws Exception {
        // 获取所有好友持有的插件钩子
        List<RobotFriend> robotFriendList = robotFriendService.list(Wrappers.<RobotFriend>lambdaQuery()
                .isNull(RobotFriend::getHoldPluginHook)
                .or().eq(RobotFriend::getHoldPluginHook, ""));
        if (CollUtil.isNotEmpty(robotFriendList)) {
            robotFriendList.stream().forEach(robotFriend -> PluginHookUtil.holdFriendPluginHook(robotFriend.getFid(), robotFriend.getHoldPluginHook()));
        }

        // 获取所有群成员持有的插件钩子
        List<RobotGroupMember> robotGroupMemberList = robotGroupMemberService.list(Wrappers.<RobotGroupMember>lambdaQuery()
                .isNull(RobotGroupMember::getHoldPluginHook)
                .or().eq(RobotGroupMember::getHoldPluginHook, ""));
        if (CollUtil.isNotEmpty(robotGroupMemberList)) {
            robotGroupMemberList.stream().forEach(robotGroupMember -> PluginHookUtil.holdGroupMemberPluginHook(robotGroupMember.getGid(), robotGroupMember.getMid(), robotGroupMember.getHoldPluginHook()));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
