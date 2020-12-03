package com.molicloud.mqr.common.enums;

/**
 * 配置类型枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/20 5:21 下午
 */
public enum SettingTypeEnum {

    /**
     * 系统配置
     */
    SYSTEM(1, "系统"),

    /**
     * 插件配置
     */
    PLUGIN(2, "插件");

    private Integer value;

    private String remark;

    SettingTypeEnum(Integer value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public Integer getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }
}