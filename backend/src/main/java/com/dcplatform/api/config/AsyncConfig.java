package com.dcplatform.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); 
        executor.setMaxPoolSize(10); 
        executor.setQueueCapacity(100); 
        executor.setThreadNamePrefix("MailThread-");
        executor.initialize();
        return executor;
    }
}