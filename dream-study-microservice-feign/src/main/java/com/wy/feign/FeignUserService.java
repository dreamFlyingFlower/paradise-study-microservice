package com.wy.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.wy.configs.FeignConfig;
import com.wy.configs.FeignSecurityConfig;
import com.wy.fallback.UserFallback;

/**
 * FeignClient:使用注解的方式进行类似轮询的负载均衡调用
 * 
 * {@link FeignClient#value()}:指定负载均衡调用的服务名
 * {@link FeignClient#configuration()}:指定Feign的自定义配置上下文,见{@link FeignConfig}
 * {@link FeignClient#fallback()}:指定Hystrix断路器降级熔断时的调用方法,只能指定方法,不能处理异常,超时等信息
 * {@link FeignClient#fallbackFactory()}:作用同fallback(),但是可以处理远程调用的异常以及一些自定义操作,
 * 该属性指向的类必须实现{@link feign.hystrix.FallbackFactory<T>},而泛型则是当前接口
 * {@link FeignClient#fallback()},{@link FeignClient#fallbackFactory()}同时存在时,fallback()优先级高,也可能出现其他错误
 * 
 * 继承FeignService接口,但是feign暂不支持获取接口上的requestmapping注解,必须重写实现接口
 * feign中不需要任何其他的实体类.直接可用object代替,客户端可正常使用get,post的restful方式接收参数
 * 之所以要继承FeignService,是为了使用通用方法减少重复代码的使用
 * 
 * 若getmapping或postmapping注解不可使用,可改成requestmapping,传多参数,对象到客户端时,必须是post方式
 * 
 * hystrix自定义断路器:必须指定configuration属性
 * 
 * @author 飞花梦影
 * @date 2021-09-21 16:24:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
// @FeignClient(value = "dream-study-microservice-service", configuration =
// FeignClientConfiguration.class,
// fallbackFactory = UserFallback.class)
@FeignClient(value = "dream-study-microservice-service", configuration = FeignSecurityConfig.class,
		fallbackFactory = UserFallback.class)
public interface FeignUserService extends FeignService {

	@Override
	@PostMapping("user/create")
	Object create(@RequestBody Object entity);

	@Override
	@GetMapping("user/remove/{id}")
	// @RequestLine("GET /user/remove/{id}")
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
	 * 
	 * @param username 参数1
	 * @param age 参数2
	 * @return
	 */
	@GetMapping
	Object getByParams(@RequestParam("username") String username, @RequestParam("age") Integer age);
}