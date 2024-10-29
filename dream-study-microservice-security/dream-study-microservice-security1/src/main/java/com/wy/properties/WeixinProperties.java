package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 微信配置类
 * @author ParadiseWY
 * @date 2019年9月26日
 */
@Getter
@Setter
public class WeixinProperties {

	private String providerId = "weixin";

	private String appId;

	private String appSecret;
}