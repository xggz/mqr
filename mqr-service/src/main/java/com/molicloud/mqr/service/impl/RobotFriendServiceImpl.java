package com.molicloud.mqr.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.molicloud.mqr.entity.RobotFriend;
import com.molicloud.mqr.mapper.RobotFriendMapper;
import com.molicloud.mqr.service.RobotFriendService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 机器人好友 服务实现类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Service
public class RobotFriendServiceImpl extends ServiceImpl<RobotFriendMapper, RobotFriend> implements RobotFriendService {

    @Override
    public void handlerHoldAction(String fid, boolean action, String pluginHookName) {
        RobotFriend robotFriend = new RobotFriend();
        robotFriend.setFid(fid);
        if (action) {
            robotFriend.setHoldPluginHook(pluginHookName);
        } else {
            robotFriend.setHoldPluginHook("");
        }
        this.saveOrUpdate(robotFriend, Wrappers.<RobotFriend>lambdaUpdate().eq(RobotFriend::getFid, fid));
    }
}
