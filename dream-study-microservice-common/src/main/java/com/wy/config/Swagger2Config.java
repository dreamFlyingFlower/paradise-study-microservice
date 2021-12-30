package com.wy.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
 * 使用swagger2自动生成文档,文档查看地址http://ip:port/swagger-ui.html#/,
 * 导出可见:{@link http://www.leftso.com/blog/402.html}
 * 
 * 若字段以大写开头,则需要加上jackjson以下2个注解,否则swagger2将无法显示字段注释:
 * {@link JsonNaming}:JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class),表示字段以大写开头
 * {@link JsonAutoDetect}:JsonAutoDetect(fieldVisibility=Visibility.ANY),检测所有修饰符字段
 * 
 * {@link EnableOpenApi}:Swagger3.0需要使用该注解打开网页浏览,且浏览地址变为ip:port/swagger-ui/index.html
 *
 * @author 飞花梦影
 * @date 2020-12-05 23:48:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@EnableOpenApi
@Profile({ "dev" })
public class Swagger2Config {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("API文档").host("localhost:55555").apiInfo(apiInfo())
				.select().apis(RequestHandlerSelectors.basePackage("com.wy.crl")).paths(PathSelectors.any()).build()
				// 忽略指定类
				.ignoredParameterTypes()
				// 为请求的header中自动添加参数
				.globalRequestParameters(authorizationParameter()).securityContexts(securityContext())
				.securitySchemes(securitySchemes());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("接口文档").description("通用接口文档").version("1.0").build();
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