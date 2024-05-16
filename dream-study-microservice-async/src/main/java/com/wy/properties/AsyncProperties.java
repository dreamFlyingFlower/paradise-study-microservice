package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 异步数据源配置
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:53:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@ConfigurationProperties(prefix = "dream.async")
@Getter
@Setter
public class AsyncProperties {

	private boolean enabled;

	private AsyncExecute asyncExecute = new AsyncExecute();

	private AsyncLogin asyncLogin = new AsyncLogin();

	@Getter
	@Setter
	public static class AsyncLogin {

		/**
		 * 是否验证登录,默认false
		 */
		private boolean enabled = false;

		/**
		 * 登录URL地址
		 */
		private String url;
	}

	@Data
	public static class AsyncExecute {

		/**
		 * 异步执行最大次数,默认5
		 */
		private int count = 5;

		/**
		 * 重试最大查询数量,默认100
		 */
		private int retryLimit = 100;

		/**
		 * 每次补偿最大查询数量.默认100
		 */
		private int compensateLimit = 100;

		/**
		 * 执行成功是否删除记录.默认删除
		 */
		private boolean deleted = true;
	}
}