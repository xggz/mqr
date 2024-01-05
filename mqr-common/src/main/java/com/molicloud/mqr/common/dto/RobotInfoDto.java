package com.molicloud.mqr.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 机器人信息保存入参
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/21 4:28 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RobotInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String qq;

    private String password;

    private String nickname;

    private String[] admins;
}
