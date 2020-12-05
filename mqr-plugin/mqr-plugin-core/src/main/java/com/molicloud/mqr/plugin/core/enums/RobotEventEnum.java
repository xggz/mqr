package com.molicloud.mqr.plugin.core.enums;

import java.util.Arrays;

/**
 * 机器人事件枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:55 下午
 */
public enum RobotEventEnum {

    /**
     * 好友消息
     */
    FRIEND_MSG,

    /**
     * 群消息
     */
    GROUP_MSG,

    /**
     * 群成员的临时消息
     */
    TEMP_MSG;

    /**
     * 机器人消息类型的事件枚举
     */
    public static final RobotEventEnum[] robotMessageEventEnums = new RobotEventEnum[]{
            FRIEND_MSG, GROUP_MSG, TEMP_MSG
    };

    /**
     * 判断枚举是否为消息类型的事件
     *
     * @return
     */
    public boolean isMessageEvent() {
        return Arrays.stream(robotMessageEventEnums).anyMatch(robotEventEnum -> robotEventEnum.equals(this));
    }
}