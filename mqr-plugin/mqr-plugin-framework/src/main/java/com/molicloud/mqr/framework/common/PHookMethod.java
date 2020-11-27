package com.molicloud.mqr.framework.common;

import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 插件钩子方法的载体
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 7:18 下午
 */
public class PHookMethod {

    private final Object bean;

    private final Method method;

    public PHookMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    /**
     * 执行插件方法
     *
     * @param pluginParam 插件执行入参
     * @return 插件执行结果
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public PluginResult execute(PluginParam pluginParam) throws InvocationTargetException, IllegalAccessException {
        Object pluginResult = method.invoke(bean, pluginParam);
        if (pluginResult == null || !(pluginResult instanceof PluginResult)) {
            throw new RuntimeException("返回类型错误");
        }
        return (PluginResult) pluginResult;
    }
}
