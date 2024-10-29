package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Session操作
 *
 * @author 飞花梦影
 * @date 2024-10-29 10:35:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class SessionInformationExpiredStrategyConfig {

	@Bean
	SessionExpiredStrategy defaultSessionInformationExpiredStrategy() {
		return new SessionExpiredStrategy();
	}
}