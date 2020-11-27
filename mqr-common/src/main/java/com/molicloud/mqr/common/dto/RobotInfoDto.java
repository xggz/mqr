package com.molicloud.mqr.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value="RobotInfoDto对象", description="机器人信息保存入参")
public class RobotInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "QQ号")
    private String qq;

    @ApiModelProperty(value = "QQ密码")
    private String password;

    @ApiModelProperty(value = "账号昵称")
    private String nickname;

    @ApiModelProperty(value = "管理员QQ")
    private String[] admins;
}