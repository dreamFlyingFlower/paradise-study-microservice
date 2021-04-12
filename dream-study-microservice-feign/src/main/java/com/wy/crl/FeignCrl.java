package com.wy.crl;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.wy.result.Result;
import com.wy.service.FeignService;

/**
 * 通用接口,前缀由各个继承本类的子类填写
 * 
 * hystrix熔断方法的参数必须和被熔断的方法参数相同,返回类型可不同,这种方法只适用于单一接口
 * 
 * hystrix断路器发生的条件:5秒内调用接口失败超过20次,可调节.
 * 
 * @author 飞花梦影
 * @date 2021-01-06 16:54:31
 * @git {@link https://github.com/mygodness100}
 */
public abstract class FeignCrl {

	public abstract FeignService getService();

	@PostMapping("create")
	public Object create(@RequestBody Map<String, Object> entity) {
		return getService().create(entity);
	}

	@DeleteMapping("remove/{id}")
	Object remove(@PathVariable("id") String id) {
		return getService().remove(id);
	}

	@PostMapping("removes")
	Object removes(@RequestBody List<String> ids) {
		return getService().removes(ids);
	}

	@PutMapping("edit")
	Object edit(@RequestBody Map<String, Object> entity) {
		return getService().edit(entity);
	}

	/**
	 * 配置hystrix的超时时间等配置,需要参照{@link HystrixCommandProperties}中的属性,默认超时时间是1秒,根据业务修改
	 * 
	 * execution.isolation.thread.timeoutInMilliseconds:超时配置<br>
	 * circuitBreaker.enabled:是否开启断路器<br>
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
	 * @param id 需要查询的编号
	 * @return 详情
	 */
	@GetMapping("getById/{id}")
	@HystrixCommand(fallbackMethod = "fallbackMethod", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
	Object getById(@PathVariable("id") String id) {
		return getService().getById(id);
	}

	public Result<?> fallbackMethod(@PathVariable("id") String id) {
		return Result.error("hystrix is happend");
	}

	@PostMapping("getList")
	Object getList(@RequestBody Map<String, Object> page) {
		return getService().getList(page);
	}
}