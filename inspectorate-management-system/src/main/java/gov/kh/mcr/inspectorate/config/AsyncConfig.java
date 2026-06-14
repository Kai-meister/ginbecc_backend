package gov.kh.mcr.inspectorate.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix(
                "async-task-");
        executor.setWaitForTasksToCompleteOnShutdown(
                true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler
    getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                log.error(
                        "Async error in {}: {}",
                        method.getName(),
                        ex.getMessage(), ex);
    }

    // MODE_INHERITABLETHREADLOCAL
    //  child threads inherit SecurityContext
    //  @Async threads ទទួល context ពី parent
    @PostConstruct
    public static void configureSecurityContext() {
        SecurityContextHolder.setStrategyName(
                SecurityContextHolder
                        .MODE_INHERITABLETHREADLOCAL);
    }
}