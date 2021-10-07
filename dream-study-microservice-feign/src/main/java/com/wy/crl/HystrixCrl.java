package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.wy.result.Result;
import com.wy.service.UserService;

/**
 * Hystrix配置
 * 
 * Hystrix触发流程:
 * 
 * <pre>
 * 请求并发是否达到最小请求数
 * ->如果达到,开始判断请求线程是否达到错误率阈值,默认是50%
 * ->如果达到错误率阈值,断路器熔断
 * ->熔断超过指定时间(默认5秒)之后放过请求,如果该请求调用成功率达到20%,则回到健康状态
 * </pre>
 * 
 * {@link HystrixCommand}:服务降级注解,超时和异常都会触发服务降级熔断,该注解只适用于单一接口
 * {@link HystrixCommand#fallbackMethod()}:服务降级时调用的方法,该方法必须和服务在同一个类中,方法参数相同
 * {@link HystrixCommand#commandProperties()}:自定义服务降级配置,比如超时时间等
 * {@link HystrixCommand#ignoreExceptions()}:是否忽略某种异常,不触发Hystrix的服务降级
 * {@link HystrixCommand#groupKey()}:当隔离策略为线程隔离时,指定线程所属的组标识
 * {@link HystrixCommand#threadPoolKey()}:线程隔离策略时,指定线程池标识,Hystrix会使用该标识来统计线程占用是否超过
 * {@link HystrixCommand#threadPoolProperties()}:线程隔离策略时,指定线程池属性{@link HystrixThreadPoolProperties},
 * 可以在配置文件中配置,前缀为hystrix.threadpool,default或hystrix.threadpool.threadPoolKey(自定义的key),
 * 多个配置时会覆盖掉默认(default)的配置,但是只会使用一种最先配置的自定义配置
 * 
 * Hystrix断路器发生的条件:5秒内调用接口失败超过20次,可调节
 * 
 * 自定义Hystrix的配置,需要参照{@link HystrixCommandProperties}中的属性,默认超时时间是1秒,根据业务修改
 * 文档地址:{@link https://github.com/Netflix/Hystrix/wiki/Configuration }
 * 该类中的所有属性都可以用在{@link HystrixCommand#commandProperties()}中
 * 
 * <pre>
 * Execution相关:
 * execution.isolation.strategy:隔离策略,默认是线程隔离.根据文档说明只有THREAD和SEMAPHORE2种模式
 * 		信号量隔离:轻量,无额外开销.但是不支持任务排队,主动超时和异步调用.适用于受信客户(内部服务),高扇出(网关),高频高速调用(cache)
 * 		线程池隔离:支持排队,超时,异步调用,但是会产生额外的开销.适用于不授信客户,有限扇出
 * execution.timeout.enabled:是否使用超时配置.默认true使用,false禁止
 * execution.isolation.thread.timeoutInMilliseconds:超时配置,默认1000ms.如果该配置没有生效,
 * 		可能是因为ribbon和hystrix都使用超时策略时,哪一个时间短就使用哪一个,
 * 		此时需要同时对ribbon的ribbon.ReadTimeout,ribbon.ConnectTimeout进行配置
 * execution.isolation.thread.interruptOnTimeout:发生超时是是否中断,默认true
 * execution.isolation.semaphore.maxConcurrentRequests:最大并发请求数,默认10.隔离策略为SEMAPHORE策略时才有效.
 * 		如果达到最大并发请求数,请求会被拒绝.
 * 		理论上选择semaphore size的原则和选择thread size一致,但选用semaphore时每次执行的单元要小且执行速度快(ms级别),
 * 		否则应该用thread,semaphore应该占整个容器的线程池的一小部分
 * 
 * Fallback相关,这些参数可以应用于Hystrix的THREAD和SEMAPHORE策略:
 * fallback.enabled:当执行失败或者请求被拒绝,是否会尝试调用hystrixCommand.getFallback(),默认true
 * fallback.isolation.semaphore.maxConcurrentRequest:若并发数达到该值,请求会被拒绝并抛出异常,fallback也不调用,默认10
 *
 * Circuit Breaker相关:
 * circuitBreaker.enabled:是否开启断路器,用来跟踪circuit的健康性,如果未达标则让request短路,默认true
 * circuitBreaker.errorThresholdPercentage:断路器打开的错误百分比,若超过该值,继续打开,否则,断路器关闭,默认50
 * circuitBreaker.sleepWindowInMilliseconds:断路器打开时,请求会直接返回降级策略,经过多久会再次请求原服务,默认5000ms
 * 		当请求原服务的比例达到一定值时,判断断路器是否需要关闭
 * circuitBreaker.requestVolumeThreshold:在断路器打开时,若想判断原服务是否恢复,所需要判断的最小请求数,默认20
 * 		即当一个rolling window的时间内(如10秒)收到19个请求,即使19个请求都失败,也不会触发circuit break.
 * 		如果requestVolumeThreshold设置为20,errorThresholdPercentage为50%,sleepWindowInMilliseconds为10,
 * 		则断路器打开10s后,请求将重新定位到原服务,若接下来的20个请求中,有超过50%的请求失败,那么断路器依然打开
 * circuitBreaker.forceOpen:强制打开断路器,如果为true,那么拒绝所有request,默认false
 * circuitBreaker.forceClosed:强制关闭断路器,如果为true,circuit将一直关闭且忽略circuitBreaker.errorThresholdPercentage
 * 
 * Metrics相关:
 * metrics.rollingStats.timeInMilliseconds:设置统计的时间窗口值,默认10000ms
 * 		circuit的打开会根据1个rolling window的统计来计算,若rolling window被设为10000毫秒,
 * 		则rolling window会被分成n个buckets,每个bucket包含success,failure,timeout,rejection的次数的统计信息
 * metrics.rollingStats.numBuckets:设置一个rolling window被划分的数量,默认10.若numBuckets=10,rolling window=10000,
 * 		那么一个bucket的时间即1秒,必须符合rolling window % numberBuckets == 0
 * metrics.rollingPercentile.enabled:执行时是否enable指标的计算和跟踪,默认true
 * metrics.rollingPercentile.timeInMilliseconds:设置rolling  percentile window的时间,默认60000ms
 * metrics.rollingPercentile.numBuckets:设置rolling percentile  window的numberBuckets,逻辑同上,默认6
 * metrics.rollingPercentile.bucketSize:如果bucket size=100,window=10s,若这10s里有500次执行,
 * 		只有最后100次执行会被统计到bucket里去,增加该值会增加内存开销以及排序 的开销,默认100
 * metrics.healthSnapshot.intervalInMilliseconds:记录health快照(用来统计成功和错误绿)的间隔,默认500ms
 * 
 * Request Context相关:
 * requestCache.enabled:需要重载getCacheKey(),返回null时不缓存,默认true
 * requestLog.enabled:记录日志到HystrixRequestLog,默认true
 * 
 * 上述Execution,Fallback,Circuit,Request也可以在配置文件中配置,前缀是hystrix.command.default,该配置会全局生效
 * 
 * Collapser Properties相关,配置文件中默认前缀为hystrix.collapser.default:
 * maxRequestsInBatch:单次批处理的最大请求数,达到该数量触发批处理,默认Integer.MAX_VALUE
 * timerDelayInMilliseconds:触发批处理的延迟,也可以为创建批处理的时间+该值,默认10
 * requestCache.enabled:是否对HystrixCollapser.execute()和HystrixCollapser.queue()的进行cache,默认true
 * 
 * ThreadPool相关,配置文件中默认前缀为hystrix.threadpool.default:
 * 线程数默认值10适用于大部分情况(有时可以设置得更小),如果需要设置得更大,那有个基本得公式可以参照如下:
 * 		requests per second at peak when healthy × 99th percentile latency in seconds + some  breathing room
 * 		每秒最大支撑的请求数(99%平均响应时间 + 缓存值)
 * 		比如:每秒能处理1000个请求,99%的请求响应时间是60ms,那么公式是:1000(0.060+0.012)
 * 		基本得原则是保持线程池尽可能小,主要是为了释放压力,防止资源被阻塞.
 * 		当一切都是正常的时候,线程池一般仅会有1到2个线程激活来提供服务
 * coreSize:并发执行的最大线程数,默认10
 * maxQueueSize:BlockingQueue的最大队列数,当设为-1,会使用SynchronousQueue,值为正在使用LinkedBlcokingQueue长度
 * 		该设置只会在初始化时有效,之后不能修改threadpool的queue size,除非重新初始化thread executor.默认-1
 * queueSizeRejectionThreshold:即使maxQueueSize没有达到最大值,达到queueSizeRejectionThreshold值后,请求也会被拒绝
 * 		因为maxQueueSize不能被动态修改,这个参数将允许动态设置该值.若maxQueueSize == -1,该字段将不起作用
 * 	keepAliveTimeMinutes:如果corePoolSize和maxPoolSize设成一样(默认实现),该设置无效
 * 		如果通过plugin(https://github.com/Netflix/Hystrix/wiki/Plugins)使用自定义实现,该设置才有用,默认1
 * metrics.rollingStats.timeInMilliseconds:线程池统计指标的时间,默认10000ms
 * metrics.rollingStats.numBuckets:将rolling window划分为n个buckets,默认10
 * 
 * 上述Collapser,ThreadPool也可以在配置文件中配置,但是会有警告,配置会全局生效
 * 
 * 配置文件中设置的Hystrix是全局配置,若想设置单个方法的,可以添加上方法的名称
 * Hystrix不允许注册多个Hystrix并发策略,但是可以通过继承{@link HystrixConcurrencyStrategy}来实现该策略
 * </pre>
 * 
 * Hystrix断路器有3种状态:
 * 
 * <pre>
 * 服务正常时断路器是关闭的(closed)
 * 服务异常时打开(open),此时服务直接返回错误或自定义返回
 * 当一段时间后(可设置),异常服务会被设置为半开启(half open)状态,此时会允许一定数量的请求到达异常服务,
 * 若请求异常服务成功的比例超过一定值,则认为服务已经恢复,此时断路器会关闭.若未超过,则恢复到open状态.
 * 
 * 发生熔断不代表服务停止,再次调用该方法的时候仍然会调用到异常的方法
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

	@HystrixCommand(fallbackMethod = "fallbackMethod",
			commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") },
			threadPoolKey = "default", threadPoolProperties = { @HystrixProperty(name = "maxQueueSize", value = "10") })
	@GetMapping("getById/{id}")
	public Object getById(@PathVariable("id") String id) {
		// 当远程调用出现异常,超时时,会使用降级方法
		return Result.ok(userService.getById(id));
	}

	/**
	 * 该方法为Hystrix的降级方法,参数必须和使用该方法的方法参数相同,返回类型可以不同
	 * 
	 * @param id 需要降级的方法的参数
	 * @param cause 当getById方法抛出异常时,会自动被hystrix捕获
	 * @return 返回值
	 */
	public Result<?> fallbackMethod(@PathVariable("id") String id, Throwable cause) {
		return Result.error("hystrix is happend");
	}
}