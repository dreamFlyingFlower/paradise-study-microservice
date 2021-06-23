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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RetryRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 * Feigin主要用来做服务调用,客户端负载均衡,流量降级,熔断.负载均衡只需要在接口上添加注解{@link FeignClient}即可
 * 
 * {@link SpringCloudApplication}可代替以下3个注解:<br>
 * {@link SpringBootApplication}:服务启动,自动配置等<br>
 * {@link EnableDiscoveryClient}:用于服务发现<br>
 * {@link EnableCircuitBreaker}:用于使用断路器<br>
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
 * Hystrix是断路器,{@link EnableHystrixDashboard}和{@link EnableHystrix}页面监控断路器,<br>
 * {@link EnableCircuitBreaker}是使用断路器需要的注解<br>
 * 
 * <pre>
 * Hystrix断路器有3种状态:
 * 服务正常时断路器是关闭的(closed)
 * 服务异常时打开(open),此时服务直接返回错误或自定义返回
 * 当一段时间后(可设置),异常服务会被设置为半开启(half open)状态,此时会允许一定数量的请求到达异常服务,
 * 若请求异常服务成功的比例超过一定值,则认为服务已经恢复,此时断路器会关闭.若未超过,则恢复到open状态.
 * 
 * 若在单个方法上添加{@link HystrixCommand#fallbackMethod()},则发生熔断fallbackMethod()必须和该方法在同一个类中,
 * 且必须参数类型相同,返回值可随意{@link com.wy.crl.FeignCrl#getById};
 * 
 * 发生熔断不代表服务停止,再次调用该方法的时候仍然会调用到异常的方法
 * </pre>
 * 
 * Turbine:类似hystrix.stream的监控,但是他监控的是整个集群的情况
 * 
 * Ribbon:负载均衡,由Feign集成,有以下几种算法:<br>
 * 
 * <pre>
 * {@link RoundRobinRule}:轮询,按顺序访问注册中心的服务器
 * {@link RandomRule}:随机,随机访问注册中心的服务器
 * {@link AvailabilityFilteringRule}:会先过滤由于多次访问故障而处于断路器跳闸状态的服务,以及并发连接数超过阈值的服务,
 * 然后对剩下的服务列表按轮询方式访问
 * {@link WeightedResponseTimeRule}:根据平均响应时间计算所有服务的权重,响应时间越快,服务权重越大,被选中的几率越高.
 * 刚启动时,若统计信息不足,则使用RoundRobinRule策略,等统计信息足够时,会切换到WeightedResponseTimeRule
 * {@link RetryRule}:先按RoundRobinRule策略获取服务,若获取服务失败,则在指定时间内会进行充实,获取可用的服务
 * {@link BestAvailableRule}:先过滤由于多次访问故障而处于断路器跳闸状态的服务,然后选择一个并发最小的服务访问
 * {@link ZoneAvoidanceRule}:默认规则,复合判断服务所在区域性能和服务的可用性来选择服务器
 * </pre>
 * 
 * @author ParadiseWY
 * @date 2020-12-08 10:52:37
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
@EnableTurbine
@EnableFeignClients
@EnableHystrixDashboard
@EnableHystrix
@EnableCircuitBreaker
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}