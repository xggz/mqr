package com.molicloud.mqr.plugin.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 持有信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/8 3:04 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HoldInfo {

    /**
     * 插件钩子名
     */
    private String hookName;

    /**
     * 持有的消息
     */
    private String message;
}