package com.molicloud.mqr.enums;

/**
 * 配置枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/20 5:21 下午
 */
public enum SettingEnums {

    /**
     * 机器人信息
     */
    ROBOT_INFO("ROBOT_INFO", "机器人配置信息");

    private String name;

    private String remark;

    SettingEnums(String name, String remark) {
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