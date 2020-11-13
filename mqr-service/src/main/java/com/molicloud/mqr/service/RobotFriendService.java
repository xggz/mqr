package com.molicloud.mqr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.molicloud.mqr.entity.RobotFriend;

/**
 * <p>
 * 机器人好友 服务类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
public interface RobotFriendService extends IService<RobotFriend> {

    /**
     * 持久化插件钩子的持有动作
     *
     * @param fid
     * @param action
     * @param pluginHookName
     */
    void handlerHoldAction(String fid, boolean action, String pluginHookName);

}
