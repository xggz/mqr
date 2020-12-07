package com.molicloud.mqr.common.define;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器人白名单配置信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/7 5:47 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RobotAllowList对象", description="机器人白名单配置信息")
public class RobotAllowList {

    @ApiModelProperty(value = "群白名单开关")
    private Boolean groupAllowListSwitch;

    @ApiModelProperty(value = "群白名单列表")
    private List<String> groupAllowList;

    @ApiModelProperty(value = "好友白名单开关")
    private Boolean friendAllowListSwitch;

    @ApiModelProperty(value = "好友白名单列表")
    private List<String> friendAllowList;
}