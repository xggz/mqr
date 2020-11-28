package com.molicloud.mqr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 计划任务线程配置
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 6:37 下午
 */
@Component
public class PluginJobConfiguration {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("pjob-task-");
//        taskScheduler.setAwaitTerminationSeconds(60);
//        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.initialize();
        return taskScheduler;
    }
}