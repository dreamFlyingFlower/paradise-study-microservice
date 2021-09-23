package com.wy.zuul;

import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 使用正则表达式来匹配路由规则
 * 
 * {@link PatternServiceRouteMapper}:自定义正则表达式在serviceId和路由之间提供匹配规则,它从serviceId提取变量并将注入到路由中
 * 接受任何正则表达式,但所有命名组都必须存在于servicePattern和routePattern中.如果servicePattern与serviceId不匹配,则使用默认行为
 * 
 * @author 飞花梦影
 * @date 2021-09-23 19:49:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class MyRegexRoute {

	@Bean
	public PatternServiceRouteMapper myRegexRoute() {
		// 第一个servicePattern是serviceId的匹配规则,第二个参数routePattern是匹配第一个参数的路由访问方式
		// 在此例中,serviceId=myusers-v1将被映射到路由/v1/myusers/**
		// 若serviceId不匹配,serviceId=myusers将被映射到路由/myusers/**(检测不到版本)此功能默认禁用,仅适用于已发现的服务
		return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)", "${version}/${name}");
	}
}