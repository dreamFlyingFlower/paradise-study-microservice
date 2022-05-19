package com.wy.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 使用Swagger3自动生成文档,文档查看地址http://ip:port/swagger-ui/index.html
 * 
 * SpringSecurity+Swagger:需要过滤Swagger相关资源,见{@link SecurityConfig#configure(WebSecurity)},{@link SecurityConfig#configure(WebSecurity)}
 * 
 * @auther 飞花梦影
 * @date 2021-07-05 23:19:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableOpenApi
@Profile({ "dev" })
public class Swagger3Config {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo()).select()
		        // 扫描指定包路径,只能写一个,不支持匹配正则
		        .apis(RequestHandlerSelectors.basePackage("com.wy.crl"))
		        // 扫描标识有指定注解的类
		        .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
		        // 扫描标识有指定注解的方法
		        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).paths(PathSelectors.any())
		        .build()
		        // 忽略指定类
		        .ignoredParameterTypes()
		        // 为请求的header中自动添加参数
		        .globalRequestParameters(authorizationParameter()).securityContexts(securityContext())
		        .securitySchemes(securitySchemes());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("登录以及认证服务器API").build();
	}

	private List<RequestParameter> authorizationParameter() {
		RequestParameterBuilder builder = new RequestParameterBuilder();
		builder.name("Authorization").description("JWT").required(false).in("header")
		        .accepts(Collections.singleton(MediaType.APPLICATION_JSON)).build();
		return Collections.singletonList(builder.build());
	}

	private List<SecurityContext> securityContext() {
		SecurityContext securityContext = SecurityContext.builder().securityReferences(defaultAuth()).build();
		return Collections.singletonList(securityContext);
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
	}

	private List<SecurityScheme> securitySchemes() {
		return Collections.singletonList(new ApiKey("JWT", "bear", "header"));
	}
}