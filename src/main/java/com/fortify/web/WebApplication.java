package com.fortify.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@Bean(name = "fortifyTaskExecutor")
	public Executor fortifyTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2); // 동시에 실행될 스캔 작업 수
		executor.setMaxPoolSize(5); // 최대 스레드 풀 크기
		executor.setQueueCapacity(10); // 큐 용량
		executor.setThreadNamePrefix("FortifyScan-");
		executor.initialize();
		return executor;
	}

}
