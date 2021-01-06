package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * feigin主要用来做服务调用,负载均衡,流量降级,熔断,负载均衡只需要在接口上添加注解{@link FeignClient}即可
 * 
 * {@link SpringCloudApplication}注解可代替 SpringBootApplication, EnableDiscoveryClient ,
 * EnableCircuitBreaker
 * 
 * 接口方法上的@GetMapping或@PostMapping注解若无效,可试试@RequestMapping
 * 
 * @apiNote 传到负载均衡的客户端实际操作类中的参数,只实验了get和post请求,不同方法参数注解不同:
 *          get:单个参数必须带@RequestParam("value"),且value的值必须填写,不能默认,否则client接收不到;
 *          若client该方法是带实体类,那么client的请求方式必须是post,否则报错;feigin默认实体类都用post;
 *          若feigin接口是get请求,实体类不带注解,client必须post方式接收参数
 *          post:单参数同get,实体类必须带@RequestBody,且client的实体类必须和feigin的实体类是同一个类
 * 
 * @apiNote 若是从feigin负载均衡请求client的参数中有实体类,请求会自动转换为post,client必须是post请求
 * 
 * @apiNote feigin接口实体类参数只能有一个,不管是何种请求类型;若传多个实体参数,需要封装到一个实体类中
 * 
 * @apiNote hystrix是断路器,{@link EnableHystrixDashboard}和{@link EnableHystrix}页面监控断路器,
 *          {@link EnableCircuitBreaker}是使用断路器需要的注解
 *          断路器有3种状态:服务正常时断路器是关闭的(closed);服务异常时打开(open),此时服务直接返回错误或自定义返回;
 *          当一段时间后(可设置),异常服务会被设置为半开启(half open)状态,此时会允许一定数量的请求到达异常服务,
 *          若请求异常服务成功的比例超过一定值,则认为服务已经恢复,此时断路器会关闭.若未超过,则恢复到open状态
 * @apiNote 若是在单个方法上添加@HystrixCommand(fallbackMethod
 *          ="fallbackMethod"),则发生熔断的方法fallbackMethod(可自定义)必须跟添加注解的方法在同一个类中,
 *          且必须参数类型相同,返回值可随意{@link com.wy.crl.FeignCrl#getById};
 * 
 * @apiNote 发生熔断不代表服务停止,再次调用该方法的时候仍然会调用到异常的方法
 * 
 * @apiNote Turbine:类似hystrix.stream的监控,但是他监控的是整个集群的情况
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