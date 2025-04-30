package com.wy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * Hystrix拦截器,重新配置Hystrix的上下文,因为类似sluenth的策略可能会使缓存不生效,需要使用自定义的策略
 *
 * @author 飞花梦影
 * @date 2025-04-30 12:40:40
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@WebFilter(asyncSupported = true, urlPatterns = "/**")
public class HystrixRequestFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// 初始化Hystrix请求上下文,在不同的上下文里,缓存不共享
		HystrixRequestContext initializeContext = HystrixRequestContext.initializeContext();

		try {
			customizeStrategy();

			chain.doFilter(request, response);
		} finally {
			initializeContext.shutdown();
		}
	}

	/**
	 * 配置Hystrix的并发策略
	 */
	public void customizeStrategy() {
		try {
			HystrixConcurrencyStrategy strategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
			if (strategy instanceof HystrixConcurrencyStrategyDefault) {
				// 如果已经就是我们想要配置的
				return;
			}
			HystrixConcurrencyStrategy target = HystrixConcurrencyStrategyDefault.getInstance();
			// 将原来其他的配置保存下来
			HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
			HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
			HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
			HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();

			// 重置配置
			HystrixPlugins.reset();
			// 将之前的配置设置回去
			HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
			HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
			HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
			HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
			// 使用默认的策略
			HystrixPlugins.getInstance().registerConcurrencyStrategy(target);
		} catch (Exception e) {
			log.error("Failed to register Hystrix Concurrenc Strategy:{}", e.getMessage(), e);
		}
	}
}