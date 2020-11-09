package com.molicloud.mqr.framework;

import cn.hutool.core.collection.CollUtil;
import com.molicloud.mqr.common.PluginExecutor;
import com.molicloud.mqr.common.annotation.PHook;
import com.molicloud.mqr.framework.common.PHookMethod;
import com.molicloud.mqr.framework.common.PluginHook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件注册，通过查找所有的插件钩子来实现
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:43 下午
 */
@Slf4j
@Component
public class PluginRegistrar implements ApplicationContextAware, SmartInitializingSingleton, Ordered {

    /**
     * ApplicationContext
     */
    private static ApplicationContext applicationContext;

    private static List<PluginHook> pluginHookList = new ArrayList<>();
    public static List<PluginHook> getPluginHookList() {
        return pluginHookList;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(PluginExecutor.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, PHook> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<PHook>) method -> AnnotatedElementUtils.findMergedAnnotation(method, PHook.class));
            } catch (Throwable ex) {
                log.error("PHook：bean[{}]查找注解[@PHook]错误", beanDefinitionName);
            }
            if (annotatedMethods == null || annotatedMethods.isEmpty()) {
                continue;
            }

            log.debug("Plugin Bean Name：{}", bean.getClass().getName());

            for (Map.Entry<Method, PHook> methodPHookEntry : annotatedMethods.entrySet()) {
                Method method = methodPHookEntry.getKey();
                PHook pHook = methodPHookEntry.getValue();
                if (pHook == null) {
                    continue;
                }
                method.setAccessible(true);
                log.debug("Plugin PHook Method：{}", method.getName());

                PluginHook pluginHook = new PluginHook();
                pluginHook.setListeningAllMessage(pHook.listeningAllMessage());
                pluginHook.setRobotEvents(new HashSet<>(Arrays.asList(pHook.robotEvents())));
                pluginHook.setKeywords(new HashSet<>(Arrays.asList(pHook.keywords())));
                pluginHook.setOrder(pHook.order());
                pluginHook.setPHookMethod(new PHookMethod(bean, method));
                pluginHookList.add(pluginHook);
            }
        }

        // 按照order字段的值对插件钩子列表进行升序排列
        if (CollUtil.isNotEmpty(pluginHookList)) {
            pluginHookList = pluginHookList.stream()
                    .sorted(Comparator.comparing(PluginHook::getOrder))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
