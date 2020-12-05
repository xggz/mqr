package com.molicloud.mqr.plugin.core.enums;

/**
 * 成员加入群的方式枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/5 3:56 下午
 */
public enum MemberJoinEnum {

    /**
     * 被邀请加入群
     */
    INVITE,

    /**
     * 成员主动加入群
     */
    ACTIVE,

    /**
     * 原群主通过 https://huifu.qq.com/ 操作
     * <br/>
     * 恢复原来群主身份并入群
     */
    RETRIEVE;
}