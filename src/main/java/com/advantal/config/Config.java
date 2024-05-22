package com.advantal.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

	// @Value("${executor.corePoolSize}")
	private Integer corePoolSize = 3;

	// @Value("${executor.maxPoolSize}")
	private Integer maxPoolSize = 3;

	// @Value("${executor.keepAliveTime}")
	private Long keepAliveTime = 200l;

	@Bean
	public WebClient webClient() {
		final int size = 16 * 1024 * 1024;
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size)).build();
		return WebClient.builder().exchangeStrategies(strategies).build();
	}

	@Bean
	public Executor executor() {
		/*
		 * ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)
		 * Executors.newFixedThreadPool(25); return threadPoolExecutor;
		 */
		ExecutorService executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		return executor;
	}
}
