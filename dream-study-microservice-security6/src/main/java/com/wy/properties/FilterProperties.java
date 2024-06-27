package com.wy.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 各种拦截,验证配置
 * 
 * @author 飞花梦影
 * @date 2023-03-15 09:22:21
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "user.filter")
public class FilterProperties {

	/**
	 * 是否拦截token
	 */
	private boolean token = false;

	/**
	 * 需要进行拦截token的url
	 */
	private List<String> tokenUrls;

	/**
	 * 不需要进行拦截token的url
	 */
	private List<String> unTokenUrls;
}