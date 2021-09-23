package com.wy.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RetryRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 * 配置自定义的负载均衡,默认是轮询.需要{@link RibbonClient}使用

 * <pre>
 * {@link ZoneAvoidanceRule}:默认策略,综合判断服务节点所在区域的性能和服务节点的可用性,来决定选择哪个服务
 * {@link RandomRule}:随机,随机访问注册中心的服务器
 * {@link RoundRobinRule}:轮询,按顺序访问注册中心的服务器
 * {@link AvailabilityFilteringRule}:先过滤掉由于多次访问故障的服务,以及并发连接数超过阈值的服务,然后对剩下的服务按照轮询策略进行访问
 * {@link WeightedResponseTimeRule}:根据平均响应时间计算所有服务的权重,响应时间越快服务权重就越大被选中的概率越高,
 * 		如果服务刚启动时统计信息不足,则使用RoundRobinRule策略,待统计信息足够会切换到WeightedResponseTimeRule策略
 * {@link RetryRule}:先按照RoundRobinRule策略分发,如果分发到的服务不能访问,则在指定时间内进行重试,然后分发其他可用的服务
 * {@link BestAvailableRule}:先过滤掉由于多次访问故障而处于断路器状态的服务,然后选择一个并发量最小的服务
 * </pre>
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