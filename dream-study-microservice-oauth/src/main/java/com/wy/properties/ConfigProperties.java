package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户自定义配置
 * 
 * @auther 飞花梦影
 * @date 2021-07-04 18:22:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "config")
public class ConfigProperties {

	private OAuth2ResourceProperties auth2Resource = new OAuth2ResourceProperties();
}