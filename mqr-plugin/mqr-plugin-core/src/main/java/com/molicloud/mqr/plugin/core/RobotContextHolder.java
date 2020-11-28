package com.molicloud.mqr.plugin.core;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.molicloud.mqr.plugin.core.define.RobotDef;
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
    private final ThreadLocal<RobotDef> robotHolder = new TransmittableThreadLocal<>();

    /**
     * 设置机器人信息
     *
     * @param robotDef
     */
    public void setRobot(RobotDef robotDef) {
        robotHolder.set(robotDef);
    }

    /**
     * 获取机器人信息
     *
     * @return
     */
    public RobotDef getRobot() {
        return robotHolder.get();
    }

    /**
     * 清理机器人信息
     */
    public void clear() {
        robotHolder.remove();
    }
}