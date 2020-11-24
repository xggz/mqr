package com.molicloud.mqr.enums;

import java.util.Arrays;

/**
 * @author feitao yyimba@qq.com
 * @since 2020/11/23 2:29 下午
 */
public enum RobotStateEnum {

    /**
     * 未启动
     */
    NOT_ENABLED(1, "未启动"),

    /**
     * 登录需要验证
     */
    TO_VERIFIED(2, "登录需要验证"),

    /**
     * 登录中
     */
    LOGGING(3, "登录中"),

    /**
     * 运行中
     */
    ONLINE(4, "运行中"),

    /**
     * 已离线
     */
    OFFLINE(9, "已离线");

    private Integer value;

    private String explain;

    RobotStateEnum(Integer value, String explain) {
        this.value = value;
        this.explain = explain;
    }

    public Integer getValue() {
        return value;
    }

    public String getExplain() {
        return explain;
    }

    /**
     * 可以启动机器人运行的状态，其它状态的机器人无法启动
     */
    public static RobotStateEnum[] canRunStateEnums = new RobotStateEnum[]{
            NOT_ENABLED, OFFLINE
    };

    /**
     * 是否能启动机器人运行
     *
     * @param state
     * @return
     */
    public static boolean isCanRun(Integer state) {
        return Arrays.stream(canRunStateEnums).anyMatch(stateEnum -> stateEnum.getValue().equals(state));
    }
}