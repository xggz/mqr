package com.molicloud.mqr.framework.common;

import com.molicloud.mqr.common.plugin.enums.RobotEventEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 插件钩子信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/5 4:57 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginHook {

    /**
     * 钩子名
     */
    private String name;

    /**
     * 是否监听所有消息
     */
    private Boolean listeningAllMessage;

    /**
     * 消息中的触发关键字列表
     */
    private Set<String> keywords;

    /**
     * 触发事件列表
     */
    private Set<RobotEventEnum> robotEvents;

    /**
     * 执行优先级，值越小优先级越高
     */
    private Integer order;

    /**
     * 插件钩子方法的载体
     */
    private PHookMethod pHookMethod;
}
