package com.molicloud.mqr.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 机器人信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/20 5:02 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RobotInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String qq;

    private String nickname;

    private String password;

    private String[] admins;

    private Integer state;
}
