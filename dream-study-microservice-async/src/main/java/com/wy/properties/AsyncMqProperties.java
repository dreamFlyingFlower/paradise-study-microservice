package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 异步数据源配置
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:53:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@ConfigurationProperties(prefix = "dream.async.mq")
@Getter
@Setter
public class AsyncMqProperties {

	/**
	 * 自定义队列名称前缀,默认应用名称
	 */
	private String topic;
}