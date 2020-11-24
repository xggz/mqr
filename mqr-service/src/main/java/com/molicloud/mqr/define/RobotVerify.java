package com.molicloud.mqr.define;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器人验证类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/23 5:31 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RobotVerify {

    /**
     * 机器人验证类型枚举
     *
     * @see com.molicloud.mqr.enums.RobotVerifyEnum
     */
    private String type;

    /**
     * 验证内容
     */
    private String content;
}