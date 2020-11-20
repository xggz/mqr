package com.molicloud.mqr.vo;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "qq号码")
    private String qq;

    @ApiModelProperty(value = "qq昵称")
    private String nickname;

    @ApiModelProperty(value = "在线状态")
    private Integer state;
}