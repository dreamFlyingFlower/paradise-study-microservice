package com.wy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 全局Web配置
 *
 * @author 飞花梦影
 * @date 2024-07-01 20:44:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class GlobalWebConfig {

	@Bean
	@ConditionalOnMissingBean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// 允许跨域的站点
		corsConfiguration.addAllowedOrigin("*");
		// 允许跨域的请求头
		corsConfiguration.addAllowedHeader("*");
		// 允许跨域的请求类型
		corsConfiguration.addAllowedMethod("*");
		// 允许携带凭证
		corsConfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
}