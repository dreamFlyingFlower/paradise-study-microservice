package com.wy.spring;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

/**
 * 自定义简单的Nacos配置
 *
 * @author 飞花梦影
 * @date 2024-05-09 09:25:25
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class NacosConfig {

	@Value("${spring.cloud.nacos.server-addr}")
	private String serverAddr;

	@Value("${spring.cloud.nacos.config.namespace}")
	private String namespace;

	public ConfigService configService() throws NacosException {
		Properties properties = new Properties();
		properties.put("serverAddr", serverAddr);
		properties.put("namespace", namespace);
		return NacosFactory.createConfigService(properties);
	}
}