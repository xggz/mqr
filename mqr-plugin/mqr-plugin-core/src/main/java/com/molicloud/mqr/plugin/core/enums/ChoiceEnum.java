package com.molicloud.mqr.plugin.core.enums;

/**
 * 可选择的类型枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:55 下午
 */
public enum ChoiceEnum {

    /**
     * 同意
     */
    ACCEPT,

    /**
     * 拒绝
     */
    REJECT("", false),

    /**
     * 忽略
     */
    IGNORE(false);

    private String message;

    private Boolean blacklist;

    ChoiceEnum() {}

    ChoiceEnum(Boolean blacklist) {
        this.blacklist = blacklist;
    }

    ChoiceEnum(String message, Boolean blacklist) {
        this.message = message;
        this.blacklist = blacklist;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(Boolean blacklist) {
        this.blacklist = blacklist;
    }
}