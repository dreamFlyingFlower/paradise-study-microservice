package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 使用swagger2自动生成文档,文档查看地址http://ip:port/swagger-ui.html#/,
 * 导出可见:{@link http://www.leftso.com/blog/402.html}
 * 
 * 注意:在使用的时候,swagger-annotations和swagger-models要用1.5.21版本的,否则会有问题
 * ignoredParameterTypes:忽略某个类,可连写多个
 * 
 * 若字段以大写开头,则需要加上jackjson以下2个注解,否则swagger2将无法显示字段注释:
 * {@link JsonNaming}:JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class),表示字段以大写开头
 * {@link JsonAutoDetect}:JsonAutoDetect(fieldVisibility=Visibility.ANY),检测所有修饰符字段
 *
 * @author ParadiseWY
 * @date 2020-12-05 23:48:02
 * @git {@link https://github.com/mygodness100}
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("通用文档").host("localhost:5555").apiInfo(apiInfo())
				.select().apis(RequestHandlerSelectors.basePackage("com.wy.crl")).paths(PathSelectors.any()).build()
				.ignoredParameterTypes();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("接口文档").description("通用接口文档").version("1.0").build();
	}
}