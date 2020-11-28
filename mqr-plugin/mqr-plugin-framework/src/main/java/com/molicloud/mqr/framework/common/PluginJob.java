package com.molicloud.mqr.framework.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件计划任务信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 6:19 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginJob {

    /**
     * 钩子名
     */
    private String name;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 插件钩子方法的载体
     */
    private PluginMethod pluginMethod;
}