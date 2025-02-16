package com.wy.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

/**
 * Resilience4j测试API
 * 
 * {@link Retry}:重试.该注解是resilience4j中的注解,不是spring-retry的注解.重试配置
 * {@link TimeLimiter}:时间限制.配合配置文件中的配置,控制请求的超时时间以及超时回调
 * {@link CircuitBreaker}:断路器配置.当某个服务或资源出现故障时,可以自动切断对该服务的调用,防止故障扩散.使用滑动窗口模式
 * {@link Bulkhead}:隔离壁.将服务或组件划分为不同的隔离,保证系统的重要部分不受其他部分故障的影响
 * ->{@link Bulkhead.Type.SEMAPHORE}:信号量模式
 * ->{@link Bulkhead.Type.THREADPOOL}:有界队列和固定大小线程池
 * {@link RateLimiter}:限流.限流如何处理检测到的多余流量,或者希望限制什么类型的请求.可以拒绝这个超限请求,或者构建一个队列,或者组合运用
 * 
 * @author 飞花梦影
 * @date 2021-09-21 16:34:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RestController
@RequestMapping("resilience4j")
public class ResilienceController {

	@GetMapping("slow")
	@Retry(name = "generalRule", fallbackMethod = "fallback")
	@CircuitBreaker(name = "generalRule", fallbackMethod = "fallback")
	@TimeLimiter(name = "generalRule", fallbackMethod = "fallback")
	public CompletableFuture<String> slowEndpoint() {
		log.info("Executing slow endpoint");
		return CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(2000); // 模拟耗时操作
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return "Slow operation completed";
		});
	}

	public String fallback(Throwable t) {
		log.warn("Fallback method called due to timeout", t);
		return "Operation timed out, please try again later.";
	}
}