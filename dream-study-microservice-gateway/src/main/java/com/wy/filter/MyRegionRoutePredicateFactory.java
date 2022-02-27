package com.wy.filter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.AfterRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.BeforeRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.BetweenRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.CookieRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.HeaderRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.HostRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.MethodRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.QueryRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.WeightRoutePredicateFactory;
import org.springframework.web.server.ServerWebExchange;

import lombok.Data;

/**
 * 自定义路由断言工厂:限制某个值的区间,类名必须以RoutePredicateFactory结尾,在配置文件中配置前缀- MyRegion = 0,100
 * 
 * Gateway内置的断言工厂
 * 
 * <pre>
 * {@link AfterRoutePredicateFactory}:接收一个日期参数,判断请求日期是否晚于指定日期,如:-After=2020-02-22T22:22:22
 * {@link BeforeRoutePredicateFactory}:接收一个日期参数,判断请求日期是否早于指定日期
 * {@link BetweenRoutePredicateFactory}:接收两个日期参数,判断请求日期是否在指定时间段内
 * {@link RemoteAddrRoutePredicateFactory}:接收一个IP地址段,判断请求主机地址是否在地址段中.如:-RemoteAddr=192.168.1.1/24
 * {@link CookieRoutePredicateFactory}:接收两个参数,cookie名和一个正则.判断请求cookie是否具有给定名称且值与正则表达式匹配
 * 		如:-Cookie=chocolate, ch.
 * {@link HeaderRoutePredicateFactory}:接收两个参数,标题名和正则.判断请求Header是否具有给定名称且值与正则表达式匹配
 * 		如:-Header=X-Request-Id, \d+
 * {@link HostRoutePredicateFactory}:接收一个参数,主机名模式.判断请求的Host是否满足匹配规则.如:-Host=**.testhost.org
 * {@link MethodRoutePredicateFactory}:接收一个参数,判断请求类型是否跟指定的类型匹配.如:-Method=GET
 * {@link PathRoutePredicateFactory}:接收一个参数,判断请求的URI部分是否满足路径规则.如:-Path=/foo/{segment}
 * {@link QueryRoutePredicateFactory}:接收两个参数,请求param和正则表达式,判断请求参数是否具有给定名称且值与正则表达式匹配
 * 		如:-Query=baz, ba.
 * {@link WeightRoutePredicateFactory}:接收一个[组名,权重],然后对于同一个组内的路由按照权重转发
 * 		routes:
 * 			-id: weight_route1 uri: host1 predicates:
 * 			-Path=/product/**
 * 			-Weight=group3, 1
 * 			-id: weight_route2 uri: host2 predicates:
 * 			-Path=/product/**
 * 			-Weight= group3, 9
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2022-02-26 18:22:39
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class MyRegionRoutePredicateFactory extends AbstractRoutePredicateFactory<Config> {

	public MyRegionRoutePredicateFactory() {
		super(Config.class);
	}

	// 用于从配置文件中获取参数值赋值到配置类中的属性上
	@Override
	public List<String> shortcutFieldOrder() {
		// 这里的顺序要跟配置文件中的参数顺序一致
		return Arrays.asList("minAge", "maxAge");
	}

	// 断言
	@Override
	public Predicate<ServerWebExchange> apply(Config config) {
		return new Predicate<ServerWebExchange>() {

			@Override
			public boolean test(ServerWebExchange serverWebExchange) {
				// 从serverWebExchange获取传入的参数
				String ageStr = serverWebExchange.getRequest().getQueryParams().getFirst("age");
				if (StringUtils.isNotEmpty(ageStr)) {
					int age = Integer.parseInt(ageStr);
					return age > config.getMin() && age < config.getMax();
				}
				return true;
			}
		};
	}
}

// 自定义一个配置类, 用于接收配置文件中的参数
@Data
class Config {

	private int min;

	private int max;
}