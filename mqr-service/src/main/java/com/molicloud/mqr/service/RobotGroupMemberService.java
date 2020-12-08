package com.molicloud.mqr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.molicloud.mqr.entity.RobotGroupMember;

/**
 * <p>
 * 机器人群成员 服务类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
public interface RobotGroupMemberService extends IService<RobotGroupMember> {

    /**
     * 持久化插件钩子的持有动作
     *
     * @param gid
     * @param mid
     * @param action
     * @param pluginHookName
     * @param message
     */
    void handlerHoldAction(String gid, String mid, boolean action, String pluginHookName, String message);
}