package com.molicloud.mqr.common.enums;

/**
 * 插件执行的触发类型枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/6 6:41 下午
 */
public enum ExecuteTriggerEnum {

    /**
     * 关键字
     */
    KEYWORD,

    /**
     * 主动持有
     */
    HOLD,

    /**
     * 默认的
     */
    DEFAULTED,

    /**
     * 监听所有消息
     */
    LISTENING_ALL_MESSAGE;
}
