package com.wy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @apiNote session
 * @author ParadiseWY
 * @date 2019年9月27日
 */
@Configuration
public class SessionInformationExpiredStrategyConfig {

	@Bean
	public SessionExpiredStrategy defaultSessionInformationExpiredStrategy() {
		return new SessionExpiredStrategy();
	}
}