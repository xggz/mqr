package com.molicloud.mqr.framework;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import lombok.experimental.UtilityClass;

/**
 * 线程上下文中存储机器人信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/23 3:53 下午
 */
@UtilityClass
public class RobotContextHolder {

    /**
     * 线程传递机器人信息
     */
    private final ThreadLocal<RobotInfoVo> robotInfoHolder = new TransmittableThreadLocal<>();

    /**
     * 设置机器人信息
     *
     * @param robotInfoVo
     */
    public void setRobotInfo(RobotInfoVo robotInfoVo) {
        robotInfoHolder.set(robotInfoVo);
    }

    /**
     * 获取机器人信息
     *
     * @return
     */
    public RobotInfoVo getRobotInfo() {
        return robotInfoHolder.get();
    }

    /**
     * 清理机器人信息
     */
    public void clear() {
        robotInfoHolder.remove();
    }

    /**
     * 修改机器人状态
     *
     * @param state
     */
    public void setState(Integer state) {
        robotInfoHolder.get().setState(state);
    }
}