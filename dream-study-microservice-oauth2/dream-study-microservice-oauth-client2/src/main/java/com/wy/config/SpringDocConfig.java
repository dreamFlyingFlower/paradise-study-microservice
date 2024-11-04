package com.wy.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * 直接使用注解集成SpringDoc,官网:https://springdoc.org/
 * 
 * SpringDoc和Swagger注解对照:https://springdoc.org/index.html#migrating-from-springfox
 * 
 * 使用注解的方式配置SpringDoc,也可以使用配置文件的方式,推荐使用配置文件
 * 
 * <pre>
 * {@link OpenAPIDefinition}:要想请求时携带登录获取的access_token,该注解中必须指定security(),并且name()要和SecurityScheme注解中的name()一致.
 * 	如果类上只有该注解或SecurityScheme注解,并且该配置类中没有任何实现,则该配置类会在本机编译时消失,请添加@Configuration注解避免这种情况
 * {@link SecuritySchemes}:可以直接替换为{@link SecurityScheme},只指定一种认证方式
 * </pre>
 * 
 * 在前端打开 http://127.0.0.1:8080/swagger-ui/index.html 访问页面,点击页面的Authorize进行认证
 * 
 * @author 飞花梦影
 * @date 2024-11-04 10:01:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@OpenAPIDefinition(info = @Info(
		// 标题
		title = "${custom.info.title}",
		// 版本
		version = "${custom.info.version}",
		// 描述
		description = "${custom.info.description}",
		// 首页
		termsOfService = "${custom.info.termsOfService}",
		// license
		license = @License(name = "${custom.license.name}",
				// license 地址
				url = "http://127.0.0.1:8080/example/test01")),
		// 这里的名字是引用下边 @SecurityScheme 注解中指定的name(),指定后发起请求时会在请求头中按照OAuth2的规范添加token
		security = @SecurityRequirement(name = "${custom.security.name}"),
		// 网关代理的路径,每个微服务都需要配置,网关不需要配置
		servers = @Server(url = "${custom.info.gateway-url}"))
@SecuritySchemes({ @SecurityScheme(
		// 指定 SecurityScheme 的名称(OpenAPIDefinition注解中的security属性中会引用该名称)
		name = "${custom.security.name}",
		// 指定认证类型为oauth2
		type = SecuritySchemeType.OAUTH2,
		// 设置认证流程
		flows = @OAuthFlows(
				// 设置授权码模式
				authorizationCode = @OAuthFlow(
						// 获取token地址
						tokenUrl = "${custom.security.token-url}",
						// 授权申请地址
						authorizationUrl = "${custom.security.authorization-url}",
						// oauth2的申请的scope(需要在OAuth2客户端中存在)
						scopes = { @OAuthScope(name = "openid", description = "OpenId登录"),
								@OAuthScope(name = "profile", description = "获取用户信息"),
								@OAuthScope(name = "message.read", description = "读"),
								@OAuthScope(name = "message.write", description = "写") }))) })
public class SpringDocConfig {

}