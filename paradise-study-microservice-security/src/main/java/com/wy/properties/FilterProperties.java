package com.wy.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 各种拦截,验证配置
 * @author ParadiseWY
 * @date 2019年9月25日
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