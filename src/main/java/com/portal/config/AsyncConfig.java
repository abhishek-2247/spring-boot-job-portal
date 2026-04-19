package com.portal.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

/**
 * Async + Scheduling Configuration.
 *
 * Thread Pool for @Async job alerts:
 *  - corePoolSize=4    : always-on worker threads
 *  - maxPoolSize=10    : burst capacity
 *  - queueCapacity=50  : tasks queue before spawning extra threads
 *  - prefix="JobAlert-": makes threads identifiable in logs/profilers
 *
 * @Scheduled tasks (e.g., daily 9AM job alerts) run on a separate
 * Spring scheduler thread, delegating heavy work to this pool via @Async.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("JobAlert-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
