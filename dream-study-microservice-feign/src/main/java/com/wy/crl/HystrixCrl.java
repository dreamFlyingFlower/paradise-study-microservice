package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.wy.result.Result;
import com.wy.service.UserService;

/**
 * Hyxtrix配置
 * 
 * hystrix熔断方法的参数必须和被熔断的方法参数相同,返回类型可不同,这种方法只适用于单一接口
 * 
 * hystrix断路器发生的条件:5秒内调用接口失败超过20次,可调节
 * 
 * 配置hystrix的超时时间等配置,需要参照{@link HystrixCommandProperties}中的属性,默认超时时间是1秒,根据业务修改
 * 文档地址:{@link https://github.com/Netflix/Hystrix/wiki/Configuration }
 * 
 * <pre>
 * execution.isolation.strategy:隔离策略,默认是线程隔离.根据文档说明只有THREAD和SEMAPHORE2种模式
 * execution.timeout.enabled:是否使用超时配置.false禁止
 * execution.isolation.thread.timeoutInMilliseconds:超时配置
 * circuitBreaker.enabled:是否开启断路器
 * circuitBreaker.requestVolumeThreshold:在断路器打开时,若想判断原服务是否恢复,所需要判断的最小请求数
 * circuitBreaker.errorThresholdPercentage:断路器打开的错误百分比,若超过该值,继续打开,否则,断路器关闭
 * circuitBreaker.sleepWindowInMilliseconds:断路器打开时,请求会直接返回降级策略,经过多长时间会再次请求原服务.
 * 当请求原服务的比例达到一定值时,判断断路器是否关闭
 * 
 * 假设requestVolumeThreshold设置为20,errorThresholdPercentage为50%,sleepWindowInMilliseconds为10,
 * 则意思为断路器打开10s后,请求将重新定位到原服务,若接下来的20个请求中,有超过50%的请求失败,那么断路器依然打开
 * 
 * 配置文件中设置的hystrix是全局配置,若想设置单个方法的,可以添加上方法的名称
 * 
 * Hystrix不允许注册多个Hystrix并发策略,但是可以通过继承{@link HystrixConcurrencyStrategy}来实现该策略
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2021-09-21 17:40:09
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@RequestMapping
public class HystrixCrl {

	@Autowired
	private UserService userService;

	@GetMapping("getById/{id}")
	@HystrixCommand(fallbackMethod = "fallbackMethod", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
	public Object getById(@PathVariable("id") String id) {
		// 当远程调用出现异常时,会使用降级方法
		return Result.ok(userService.getById(id));
	}

	/**
	 * 该方法为Hystrix的降级方法,参数必须和使用该方法的方法参数相同
	 * 
	 * @param id 需要降级的方法的参数
	 * @return 返回值
	 */
	public Result<?> fallbackMethod(@PathVariable("id") String id) {
		return Result.error("hystrix is happend");
	}
}