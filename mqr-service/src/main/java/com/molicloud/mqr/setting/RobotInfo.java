package com.molicloud.mqr.setting;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 机器人配置
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/21 4:37 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RobotInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * QQ号
     */
    private String qq;

    /**
     * QQ密码
     */
    private String password;

    /**
     * 账号昵称
     */
    private String nickname;

    /**
     * 管理员QQ
     */
    private String[] admins = new String[]{};

    /**
     * 状态（1：未启动，2：登录需要验证，3：登录中，4：运行中，9：已离线）
     */
    private Integer state = 1;
}