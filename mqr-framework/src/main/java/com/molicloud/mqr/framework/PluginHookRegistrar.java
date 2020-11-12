package com.molicloud.mqr.framework;

import cn.hutool.core.collection.CollUtil;
import com.molicloud.mqr.common.PluginExecutor;
import com.molicloud.mqr.common.annotation.PHook;
import com.molicloud.mqr.common.enums.RobotEventEnum;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 插件钩子注册，通过查找所有的插件钩子注解来实现
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:43 下午
 */
@Slf4j
@Component
public class PluginHookRegistrar implements ApplicationContextAware, SmartInitializingSingleton, Ordered {

    /**
     * ApplicationContext
     */
    private static ApplicationContext applicationContext;

    // 常规插件钩子列表
    private static List<PluginHook> normalPluginHookList = new ArrayList<>();
    // 默认插件钩子列表
    private static List<PluginHook> defaultPluginHookList = new ArrayList<>();
    // 监听所有消息的插件钩子列表
    private static List<PluginHook> listeningAllMessagePluginHookList = new ArrayList<>();

    // 插件钩子列表（插件钩子不重复）
    private static List<PluginHook> allPluginHookList = new LinkedList<>();

    /**
     * 获取所有的插件钩子（插件钩子不重复）
     *
     * @return
     */
    public static List<PluginHook> getAllPluginHookList() {
        return allPluginHookList;
    }

    /**
     * 获取监听所有消息的插件钩子列表
     *
     * @param robotEventEnum
     * @return
     */
    public static List<PluginHook> getListeningAllMessagePluginHookList(RobotEventEnum robotEventEnum) {
        return listeningAllMessagePluginHookList.stream()
                .filter(pluginHook -> pluginHook.getRobotEvents().contains(robotEventEnum))
                .collect(Collectors.toList());
    }

    /**
     * 获取常规的插件钩子列表
     *
     * @param robotEventEnum
     * @return
     */
    public static List<PluginHook> getNormalPluginHookList(RobotEventEnum robotEventEnum) {
        return normalPluginHookList.stream()
                .filter(pluginHook -> pluginHook.getRobotEvents().contains(robotEventEnum))
                .collect(Collectors.toList());
    }

    /**
     * 获取默认的插件钩子列表
     *
     * @param robotEventEnum
     * @return
     */
    public static List<PluginHook> getDefaultPluginHookList(RobotEventEnum robotEventEnum) {
        return defaultPluginHookList.stream()
                .filter(pluginHook -> pluginHook.getRobotEvents().contains(robotEventEnum))
                .collect(Collectors.toList());
    }

    /**
     * 根据插件的钩子名获取插件钩子
     *
     * @param name
     * @return
     */
    public static PluginHook getPluginHookByName(String name) {
        return allPluginHookList.stream().filter(pluginHook -> pluginHook.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    @Override
    public void afterSingletonsInstantiated() {
        loadAllPluginHook();
    }

    /**
     * 加载插件钩子
     */
    private void loadAllPluginHook() {
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
                pluginHook.setName(pHook.name());
                pluginHook.setListeningAllMessage(pHook.listeningAllMessage());
                pluginHook.setRobotEvents(new HashSet<>(Arrays.asList(pHook.robotEvents())));
                pluginHook.setKeywords(new HashSet<>(Arrays.asList(pHook.keywords())));
                pluginHook.setOrder(pHook.order());
                pluginHook.setPHookMethod(new PHookMethod(bean, method));
                // 判断是否监听所有消息钩子
                if (pHook.listeningAllMessage()) {
                    listeningAllMessagePluginHookList.add(pluginHook);
                }
                // 判断是否为默认插件钩子
                if (pHook.defaulted()) {
                    defaultPluginHookList.add(pluginHook);
                }
                // 如果关键字列表不为空，则列入常规的插件钩子
                if (pHook.keywords() != null && pHook.keywords().length > 0) {
                    normalPluginHookList.add(pluginHook);
                }
                // 把所有插件钩子存入一个列表（不重复）
                allPluginHookList.add(pluginHook);
            }
        }

        // 按照order字段的值对插件钩子列表进行升序排列
        if (CollUtil.isNotEmpty(listeningAllMessagePluginHookList)) {
            listeningAllMessagePluginHookList = listeningAllMessagePluginHookList.stream()
                    .sorted(Comparator.comparing(PluginHook::getOrder))
                    .collect(Collectors.toList());
        }
        if (CollUtil.isNotEmpty(normalPluginHookList)) {
            normalPluginHookList = normalPluginHookList.stream()
                    .sorted(Comparator.comparing(PluginHook::getOrder))
                    .collect(Collectors.toList());
        }
        if (CollUtil.isNotEmpty(defaultPluginHookList)) {
            defaultPluginHookList = defaultPluginHookList.stream()
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
