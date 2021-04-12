package com.wy.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.wy.fallback.UserFallback;

/**
 * @apiNote 继承FeignService接口,但是feign暂不支持获取接口上的requestmapping注解,必须重写实现接口
 *          feign中不需要任何其他的实体类.直接可用object代替,客户端可正常使用get,post的restful方式接收参数
 * @apiNote 若getmapping或postmapping注解不可使用,可改成requestmapping,传对象到客户端时,必须是post方式
 * @apiNote hystrix自定义断路器:必须指定configuration,同时fallbackFactory指向的类必须实现{@link feign.hystrix.FallbackFactory<T>}
 *          fallback和fallbackFactory只能同时用一个,若同时写会出现不可预知的错误
 * @author ParadiseWy
 * @date 2019年8月21日 21:47:45
 */
@FeignClient(value = "cloudclient1", configuration = FeignClientConfiguration.class, fallbackFactory = UserFallback.class)
public interface UserService extends FeignService {

	@Override
	@PostMapping("user/create")
	Object create(@RequestBody Object entity);

	@Override
	@GetMapping("user/remove/{id}")
	Object remove(@PathVariable("id") String id);

	@Override
	@PostMapping("user/removes")
	Object removes(@RequestBody List<String> ids);

	@Override
	@PostMapping("user/edit")
	Object edit(@RequestBody Object entity);

	@Override
	@GetMapping("user/getById/{id}")
	Object getById(@PathVariable("id") String id);

	@Override
	@PostMapping("user/getList")
	Object getList(@RequestBody Object page);

	@GetMapping("user/checkUnique/{username}")
	Object checkUnique(@PathVariable("username") String username);

	/**
	 * 多参数,但不形成一个对象时,可使用该方法传递参数,每一个参数都必须写注解,且value属性必须写
	 * @param username 参数1
	 * @param age 参数2
	 * @return
	 */
	@GetMapping
	Object getByParams(@RequestParam("username") String username, @RequestParam("age") Integer age);
}