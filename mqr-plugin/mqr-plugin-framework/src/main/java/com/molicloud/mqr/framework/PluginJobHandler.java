package com.molicloud.mqr.framework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 计划任务调度配置/管理
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 6:06 下午
 */
@Slf4j
@Configuration
public class PluginJobHandler implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;

    private Map<String, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap();

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
        this.taskRegistrar = taskRegistrar;
    }

    /**
     * 添加计划任务
     *
     * @param taskId
     * @param triggerTask
     */
    public void addTriggerTask(String taskId, TriggerTask triggerTask) {
        if (taskFutures.containsKey(taskId)) {
            throw new SchedulingException("TaskId：" + taskId + "已存在");
        }
        if (taskRegistrar == null) {
            throw new RuntimeException("ScheduledTaskRegistrar 初始化错误");
        }
        TaskScheduler scheduler = taskRegistrar.getScheduler();
        ScheduledFuture<?> future = scheduler.schedule(triggerTask.getRunnable(), triggerTask.getTrigger());
        taskFutures.put(taskId, future);
        log.debug("计划任务添加成功：{}", taskId);
    }

    /**
     * 取消计划任务
     *
     * @param taskId
     */
    public void cancelTriggerTask(String taskId) {
        ScheduledFuture<?> future = taskFutures.get(taskId);
        if (future != null) {
            future.cancel(true);
        }
        taskFutures.remove(taskId);
        log.debug("计划任务取消成功：{}", taskId);
    }

    /**
     * 取消所有计划任务
     */
    public void cancelAllTriggerTask() {
        taskFutures.forEach((taskId, future) -> {
            if (future != null) {
                future.cancel(true);
            }
            taskFutures.remove(taskId);
            log.debug("计划任务取消成功：{}", taskId);
        });
    }
}