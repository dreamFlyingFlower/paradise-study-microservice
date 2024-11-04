package com.wy.wechat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 让框架可以解析text/plain响应信息,微信获取用户信息时响应的类型为“text/plain”,需要特殊处理
 * 
 * Spring在解析响应体的时候会根据content-type去解析,只会将application/json的mediaType解析为bean,不管是获取token还是获取用户信息的请求,
 * 框架都是用restTemplate发起请求,并且指定了响应数据的java类,所有与微信重写相关的接口,除了获取code是重定向,其它都有响应,而微信响应回来的content-type是text/plain,
 * 框架会因为找不到解析的类型从而抛出异常,所以还要配置一下,让框架也解析text/plain数据
 *
 * @author 飞花梦影
 * @date 2024-11-04 09:30:47
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class WechatUserResponseConverter extends MappingJackson2HttpMessageConverter {

	public WechatUserResponseConverter() {
		List<MediaType> mediaTypes = new ArrayList<>(super.getSupportedMediaTypes());
		// 微信获取用户信息时响应的类型为“text/plain”,需要特殊处理
		mediaTypes.add(MediaType.TEXT_PLAIN);
		super.setSupportedMediaTypes(mediaTypes);
	}
}