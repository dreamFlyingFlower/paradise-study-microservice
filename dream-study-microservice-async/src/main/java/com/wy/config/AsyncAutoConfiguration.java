package com.wy.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.wy.properties.AsyncDataSourceProperties;

import dream.flying.flower.autoconfigure.web.properties.AsyncExecutorProperties;
import lombok.AllArgsConstructor;

/**
 * 异步线程池初始化
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:53:05
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@ConditionalOnProperty(prefix = "dream.async.executor", value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ AsyncExecutorProperties.class, AsyncDataSourceProperties.class })
@AllArgsConstructor
public class AsyncAutoConfiguration {

	@Bean("asyncJdbcTemplate")
	JdbcTemplate jdbcTemplate(AsyncDataSourceProperties asyncDataSourceConfig) throws Exception {
		return new JdbcTemplate(DruidDataSourceFactory.createDataSource(asyncDataSourceConfig));
	}

	@Bean(name = "asyncExecute")
	Executor asyncExecute(AsyncExecutorProperties asyncExecutorProperties) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(asyncExecutorProperties.getCorePoolSize());
		executor.setMaxPoolSize(asyncExecutorProperties.getMaxPoolSize());
		executor.setQueueCapacity(asyncExecutorProperties.getQueueCapacity());
		executor.setKeepAliveSeconds(asyncExecutorProperties.getKeepAliveSeconds());
		executor.setThreadNamePrefix(asyncExecutorProperties.getThreadNamePrefix());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return TtlExecutors.getTtlExecutor(executor);
	}
}