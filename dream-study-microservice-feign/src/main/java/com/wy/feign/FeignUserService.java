package com.wy.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.wy.config.FeignConfig;
import com.wy.config.FeignInterceptorConfig;
import com.wy.config.FeignSecurityConfig;

import feign.Headers;

/**
 * FeignClient:使用注解的方式进行类似轮询的负载均衡调用
 * 
 * {@link FeignClient#value()}:指定负载均衡调用的服务名
 * {@link FeignClient#contextId()}:同一个项目中相同的value只能有一个,如果必须存在多个, 可使用contextId进行区分
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
// UserFallback需要spring-cloud-starter-openfeign版本为2.2.10.RELEASE
// @FeignClient(value = "dream-study-microservice-service", configuration =
// FeignSecurityConfig.class,
// fallbackFactory = UserFallback.class)
@FeignClient(value = "dream-study-microservice-service", configuration = FeignSecurityConfig.class)
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

	/**
	 * SpringQueryMap:该注解可自动解析对象参数传入GET,不必一个一个写
	 * 
	 * @return
	 */
	@GetMapping
	Object getByParams(@SpringQueryMap Object object);

	/**
	 * 请求头设置方式:
	 * 
	 * <pre>
	 * 方式1:直接写在请求方式注解中
	 * 方式2:直接添加Headers注解
	 * 方式3:在参数数添加@RequestHeader,并填写请求头key
	 * 方式4:如果有多个请求头参数,使用MultiValueMap
	 * 方式5:实现RequestInterceptor,见{@link FeignInterceptorConfig}
	 * </pre>
	 * 
	 * @return
	 */
	@Headers({ "Content-Type: application/json;charset=UTF-8" })
	@GetMapping(headers = { "Content-Type=application/json;charset=UTF-8" })
	Object setHeader(@RequestHeader("Authorization") @RequestParam("token") String token,
			@RequestHeader MultiValueMap<String, String> headers);
}