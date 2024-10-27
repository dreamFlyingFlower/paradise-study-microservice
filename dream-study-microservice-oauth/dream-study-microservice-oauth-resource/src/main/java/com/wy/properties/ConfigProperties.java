package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义配置
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:28:07
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dream.oauth2.resource")
public class ConfigProperties {

	private SecurityProperties security = new SecurityProperties();

	private OAuth2Properties oauth2 = new OAuth2Properties();
}