package com.molicloud.mqr.framework;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.molicloud.mqr.framework.common.PluginJob;
import com.molicloud.mqr.plugin.core.PluginExecutor;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.annotation.PJob;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.framework.common.PluginMethod;
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
 * 注册插件执行器的钩子和计划任务
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:43 下午
 */
@Slf4j
@Component
public class PluginExecutorRegistrar implements ApplicationContextAware, SmartInitializingSingleton, Ordered {

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
     * 存储所有查找到的插件计划任务注解
     */
    private static List<PluginJob> pluginJobRepository = new ArrayList<>();
    public static List<PluginJob> getPluginJobRepository() {
        return pluginJobRepository;
    }

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

            Map<Method, PHook> pHookAnnotatedMethods = null;
            Map<Method, PJob> pJobAnnotatedMethods = null;
            try {
                pHookAnnotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<PHook>) method -> AnnotatedElementUtils.findMergedAnnotation(method, PHook.class));
                pJobAnnotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<PJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, PJob.class));
            } catch (Throwable ex) {
                log.error("PluginExecutor：bean[{}]查找插件执行器错误", beanDefinitionName);
            }
            log.debug("------------------------------");
            log.debug("Plugin Bean Name：{}", bean.getClass().getName());
            if (MapUtil.isNotEmpty(pHookAnnotatedMethods)) {
                for (Map.Entry<Method, PHook> methodPHookEntry : pHookAnnotatedMethods.entrySet()) {
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
                    pluginHook.setEqualsKeywords(new HashSet<>(Arrays.asList(pHook.equalsKeywords())));
                    pluginHook.setStartsKeywords(new HashSet<>(Arrays.asList(pHook.startsKeywords())));
                    pluginHook.setEndsKeywords(new HashSet<>(Arrays.asList(pHook.endsKeywords())));
                    pluginHook.setContainsKeywords(new HashSet<>(Arrays.asList(pHook.containsKeywords())));
                    pluginHook.setOrder(pHook.order());
                    pluginHook.setPluginMethod(new PluginMethod(bean, method));
                    // 判断是否监听所有消息钩子
                    if (pHook.listeningAllMessage()) {
                        listeningAllMessagePluginHookList.add(pluginHook);
                    }
                    // 判断是否为默认插件钩子
                    if (pHook.defaulted()) {
                        defaultPluginHookList.add(pluginHook);
                    }
                    // 如果关键字列表不为空，则列入常规的插件钩子
                    if (pHook.equalsKeywords().length > 0
                            || pHook.startsKeywords().length > 0
                            || pHook.endsKeywords().length > 0
                            || pHook.containsKeywords().length > 0) {
                        normalPluginHookList.add(pluginHook);
                    }
                    // 把所有插件钩子存入一个列表（不重复）
                    allPluginHookList.add(pluginHook);
                }
            }
            if (MapUtil.isNotEmpty(pJobAnnotatedMethods)) {
                for (Map.Entry<Method, PJob> methodPJobEntry : pJobAnnotatedMethods.entrySet()) {
                    Method method = methodPJobEntry.getKey();
                    PJob pJob = methodPJobEntry.getValue();
                    if (pJob == null) {
                        continue;
                    }
                    method.setAccessible(true);
                    log.debug("Plugin PJob Method：{}", method.getName());

                    PluginJob pluginJob = new PluginJob();
                    pluginJob.setId(beanDefinitionName.concat("->").concat(method.getName()));
                    pluginJob.setHookName(pJob.hookName());
                    pluginJob.setCron(pJob.cron());
                    pluginJob.setPluginMethod(new PluginMethod(bean, method));
                    pluginJobRepository.add(pluginJob);
                }
            }
            log.debug("------------------------------");
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
