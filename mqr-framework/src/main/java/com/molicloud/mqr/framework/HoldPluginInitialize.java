package com.molicloud.mqr.framework;

import com.molicloud.mqr.service.RobotFriendService;
import com.molicloud.mqr.service.RobotGroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化机器人好友和群成员持有的插件钩子
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 6:23 下午
 */
@Slf4j
@Component
public class HoldPluginInitialize implements CommandLineRunner {

    @Autowired
    private RobotFriendService robotFriendService;

    @Autowired
    private RobotGroupMemberService robotGroupMemberService;

    @Override
    public void run(String... args) throws Exception {
        // todo 加载机器人好友和群成员所持有的插件钩子
    }
}
