package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * RabbitMQ自定义配置
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:49:51
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ConfigurationProperties(prefix = "config.rabbit")
@Getter
@Setter
public class RabbitProperties {

	private DeadProperties dead = new DeadProperties();
}