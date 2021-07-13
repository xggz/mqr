package com.molicloud.mqr.plugin.aireply;

/**
 * 开放API消息类型枚举
 *
 * @author xggz <yyimba@qq.com>
 * @since 2021/7/13 10:02
 */
public enum OpenApiMessageTyped {

    /**
     * 文本
     */
    TEXT(1, "文本"),

    /**
     * 图片
     */
    IMAGE(2, "图片"),

    /**
     * 文档
     */
    DOC(3, "文档"),

    /**
     * 音频
     */
    AUDIO(4, "音频"),

    /**
     * 其它
     */
    OTHER(9, "其它");

    private Integer value;

    private String explain;

    OpenApiMessageTyped(Integer value, String explain) {
        this.value = value;
        this.explain = explain;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}