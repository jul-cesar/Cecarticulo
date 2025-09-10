package com.aej.cecarticulo.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Value("${app.threads}")// Number of threads for the thread pool
    private int threads;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threads);
    }
}