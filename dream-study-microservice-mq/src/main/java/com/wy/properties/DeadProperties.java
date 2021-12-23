package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * RabbitMQ死信队列配置
 *
 * @author 飞花梦影
 * @date 2021-12-23 14:49:59
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Getter
@Setter
public class DeadProperties {

	private String exchange = "dead-exchange";

	private String routingKey = "dead-key";

	private String queue = "dead-queue";
}