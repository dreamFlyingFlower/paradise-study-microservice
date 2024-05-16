package com.wy.properties;

import java.util.LinkedHashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步数据源配置
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:53:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@ConfigurationProperties(prefix = "dream.async.datasource")
public class AsyncDataSourceProperties extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 1L;

}