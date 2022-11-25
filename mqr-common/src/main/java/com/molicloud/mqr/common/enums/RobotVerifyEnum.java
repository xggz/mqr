package com.molicloud.mqr.common.enums;

/**
 * 机器人验证类型枚举
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/23 2:29 下午
 */
public enum RobotVerifyEnum {

    /**
     * 图片验证
     */
    IMG,

    /**
     * 二维码
     */
    QRCODE,

    /**
     * 短信
     */
    SMS,

    /**
     * 滑块
     */
    SLIDER,

    /**
     * URL地址验证
     */
    URL;
}