package com.maciu19.autobidder.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    public static final String TASK_EXECUTOR_SCRAPER = "taskExecutorScraper";

    @Bean(name = TASK_EXECUTOR_SCRAPER)
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(150);
        executor.setThreadNamePrefix("Scraper-");

        executor.initialize();
        return executor;
    }
}
