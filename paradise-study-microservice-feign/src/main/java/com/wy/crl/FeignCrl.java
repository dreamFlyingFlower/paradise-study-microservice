package com.wy.crl;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.wy.result.Result;
import com.wy.service.FeignService;

/**
 * @apiNote 通用接口,前缀由各个继承本类的子类填写
 * @apiNote hystrix熔断方法的参数必须和被熔断的方法参数相同,返回类型可不同,这种方法只适用于单一接口
 *          hystrix发生的条件:5秒内调用接口失败超过20次,可调节.
 * @author paradiseWy
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
	 * 配置hystrix的超时时间等配置,需要参照HystrixCommandProperties中的属性,默认超时时间是1秒,根据业务修改
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