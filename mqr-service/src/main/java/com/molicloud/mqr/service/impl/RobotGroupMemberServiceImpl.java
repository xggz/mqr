package com.molicloud.mqr.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.molicloud.mqr.entity.RobotGroupMember;
import com.molicloud.mqr.mapper.RobotGroupMemberMapper;
import com.molicloud.mqr.service.RobotGroupMemberService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 机器人群成员 服务实现类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Service
public class RobotGroupMemberServiceImpl extends ServiceImpl<RobotGroupMemberMapper, RobotGroupMember> implements RobotGroupMemberService {

    @Override
    public void handlerHoldAction(String gid, String mid, boolean action, String pluginHookName) {
        RobotGroupMember robotGroupMember = new RobotGroupMember();
        robotGroupMember.setGid(gid);
        robotGroupMember.setMid(mid);
        if (action) {
            robotGroupMember.setHoldPluginHook(pluginHookName);
        } else {
            robotGroupMember.setHoldPluginHook("");
        }
        this.saveOrUpdate(robotGroupMember, Wrappers.<RobotGroupMember>lambdaUpdate().eq(RobotGroupMember::getGid, gid).eq(RobotGroupMember::getMid, mid));
    }
}
