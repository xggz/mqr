package com.molicloud.mqr.common.plugin.annotation;

import com.molicloud.mqr.common.plugin.enums.RobotEventEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插件钩子
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:48 下午
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PHook {

    /**
     * 钩子名
     *
     * @return
     */
    String name();

    /**
     * 是否监听所有消息<br/>
     *
     * 监听所有消息的插件不需要匹配关键字即可优先触发
     *
     * @return
     */
    boolean listeningAllMessage() default false;

    /**
     * 消息中的触发关键字列表
     *
     * @return
     */
    String[] keywords() default "";

    /**
     * 触发事件列表
     *
     * @return
     */
    RobotEventEnum[] robotEvents();

    /**
     * 执行优先级<br/>
     *
     * 值越小优先级越高
     *
     * @return
     */
    int order() default 0;

    /**
     * 是否为默认的插件<br/>
     *
     * 如果持有的插件钩子、监听所有消息插件钩子、常规插件钩子都没有返回结果，
     * 则默认的插件不需要触发关键字，并在最后执行
     *
     * @return
     */
    boolean defaulted() default false;
}
