package com.molicloud.mqr.enums;

/**
 * 配置枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/20 5:21 下午
 */
public enum SettingEnum {

    /**
     * 机器人信息
     */
    ROBOT_INFO("ROBOT_INFO", "机器人配置信息"),

    /**
     * 机器人登录验证信息
     */
    ROBOT_LOGIN_VERIFY("ROBOT_LOGIN_VERIFY", "机器人登录验证信息"),

    /**
     * 机器人登录验证结果
     */
    ROBOT_LOGIN_VERIFY_RESULT("ROBOT_LOGIN_VERIFY_RESULT", "机器人登录验证结果");

    private String name;

    private String remark;

    SettingEnum(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }
}