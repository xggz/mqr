package com.molicloud.mqr.common.define;

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
public class RobotAllowList {

    private Boolean groupAllowListSwitch;

    private List<String> groupAllowList;

    private Boolean friendAllowListSwitch;

    private List<String> friendAllowList;
}
