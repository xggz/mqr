package com.molicloud.mqr.common.annotation;

import com.molicloud.mqr.common.enums.RobotEventEnum;

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
     * 是否监听所有消息，监听所有消息的插件不需要匹配关键字即可触发
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
     * 执行优先级，值越小优先级越高
     *
     * @return
     */
    int order() default 0;
}
