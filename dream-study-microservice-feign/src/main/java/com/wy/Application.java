package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RetryRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.wy.crl.HystrixCrl;

/**
 * Feigin主要用来做服务调用,客户端负载均衡,流量降级,熔断.负载均衡只需要在接口上添加注解{@link FeignClient}即可
 * 
 * {@link SpringCloudApplication}可代替以下3个注解:
 * 
 * <pre>
 * {@link SpringBootApplication}:服务启动,自动配置等
 * {@link EnableDiscoveryClient}:用于服务发现
 * {@link EnableCircuitBreaker}:用于使用断路器
 * </pre>
 * 
 * {@link FeignClient}修饰的接口方法上的@GetMapping或@PostMapping注解若无效,可试试@RequestMapping
 * 
 * 传到负载均衡的客户端实际操作类中的参数,只实验了get和post请求,不同方法参数注解不同:
 * 
 * <pre>
 * get:单个参数必须带{@link RequestParam#value()},且value()的值必须填写,不能默认,否则Client接收不到
 * 若Client被调用方法参数带实体类,那么Client的请求方式必须是post,否则报错;
 * 若Feigin接口是get请求,实体类不带注解,Client必须post方式接收参数.即Feigin默认实体类都用post
 * 
 * post:单参数同get,实体类必须带@RequestBody,且Client的实体类必须和Feigin的实体类是同一个类
 * 若是从Feigin负载均衡请求Client的参数中有实体类,请求会自动转换为post,Client必须是post请求
 * 
 * Feigin接口实体类参数只能有一个,不管是何种请求类型;若传多个实体参数,需要封装到一个实体类或Map中
 * </pre>
 * 
 * Hystrix:断路器,其他见{@link HystrixCrl}
 * 
 * <pre>
 * {@link EnableHystrixDashboard}:开启Web页面监控.在Web打开使用了该注解的ip:port/actuator/hystrix,
 * 		输入需要监控的微服务地址ip:port/actuator/hystrix.stream,点击监控之后可以实时监控微服务的远程调用请求情况
 * {@link EnableHystrix},{@link EnableCircuitBreaker}:作用相同,开启断路器,EnableHystrix更见名知义
 * </pre>
 * 
 * Turbine:类似hystrix.stream的监控,但是他监控的是整个集群的情况,只会监控使用Hystrix的服务.参考application-turbine.yml
 * 
 * <pre>
 * 当cluster-config的值为default时,Web页面可直接访问ip:port/turbine.stream,有请求时会显示请求详情,有延迟
 * 当cluster-config的值不是default时,Web页面访问ip:port/turbine.stream?cluster=cluster-config的值
 * 将ip:port/turbine.stream输入到Hystrix Dashboar的监控地址栏中,就可以在Web端监控整个服务集群
 * </pre>
 * 
 * Ribbon:负载均衡,由Feign集成,有以下几种算法:
 * 
 * <pre>
 * {@link RoundRobinRule}:轮询,按顺序访问注册中心的服务器
 * {@link RandomRule}:随机,随机访问注册中心的服务器
 * {@link AvailabilityFilteringRule}:先过滤由于多次访问故障而处于断路器跳闸状态,以及并发连接数超过阈值的服务,
 * 		然后对剩下的服务列表按轮询方式访问
 * {@link WeightedResponseTimeRule}:根据平均响应时间计算所有服务的权重,响应时间越快,服务权重越大,被选中的几率越高.
 * 		刚启动时,若统计信息不足,则使用RoundRobinRule策略,等统计信息足够时,会切换到WeightedResponseTimeRule
 * {@link RetryRule}:先按RoundRobinRule策略获取服务,若获取服务失败,则在指定时间内会进行重试,获取可用的服务
 * {@link BestAvailableRule}:先过滤由于多次访问故障而处于断路器跳闸状态的服务,然后选择一个并发最小的服务访问
 * {@link ZoneAvoidanceRule}:默认规则,复合判断服务所在区域性能和服务的可用性来选择服务器
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableTurbine
@EnableFeignClients
@EnableHystrixDashboard
@EnableHystrix
@EnableCircuitBreaker
@EnableDiscoveryClient
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}