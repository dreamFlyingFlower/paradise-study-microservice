package com.wy.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;

/**
 * 配置自定义的负载均衡,默认是轮询.需要{@link RibbonClient}使用
 * 
 * @author 飞花梦影
 * @date 2021-09-21 15:12:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class RibbonConfig {

	@Bean
	public IRule iRule() {
		// 指定Ribbon的负载均衡为随机模式
		return new RandomRule();
	}
}