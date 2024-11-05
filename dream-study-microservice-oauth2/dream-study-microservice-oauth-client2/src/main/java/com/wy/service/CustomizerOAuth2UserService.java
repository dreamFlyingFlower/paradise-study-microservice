package com.wy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import com.wy.strategy.OAuth2UserConverterContext;
import com.wy.vo.OAuth2ClientVO;
import com.wy.wechat.WechatUserRequestEntityConverter;
import com.wy.wechat.WechatUserResponseConverter;

/**
 * 自定义登录第三方认证服务器获取普通方式用户信息服务,可继承{@link DefaultOAuth2UserService}或实现{@link OAuth2UserService}
 *
 * @author 飞花梦影
 * @date 2024-11-03 10:48:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CustomizerOAuth2UserService extends DefaultOAuth2UserService {

	private final OAuth2ClientService oauth2ClientService;

	private final OAuth2UserConverterContext oauth2UserConverterContext;

	public CustomizerOAuth2UserService(OAuth2ClientService oauth2ClientService,
			OAuth2UserConverterContext oauth2UserConverterContext) {
		this.oauth2ClientService = oauth2ClientService;
		this.oauth2UserConverterContext = oauth2UserConverterContext;
		// 初始化时添加微信用户信息请求处理,oidcUserService本质上是调用该类获取用户信息的,不用添加
		super.setRequestEntityConverter(new WechatUserRequestEntityConverter());
		// 设置用户信息转换器
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		List<HttpMessageConverter<?>> messageConverters =
				new ArrayList<>(Arrays.asList(new StringHttpMessageConverter(), new WechatUserResponseConverter(),
						new ResourceHttpMessageConverter(), new ByteArrayHttpMessageConverter(),
						new AllEncompassingFormHttpMessageConverter()));
		// List<HttpMessageConverter<?>> messageConverters = List.of(new
		// StringHttpMessageConverter(),
		// 获取微信用户信息时使其支持“text/plain”
		// new WechatUserResponseConverter(), new ResourceHttpMessageConverter(),
		// new ByteArrayHttpMessageConverter(), new
		// AllEncompassingFormHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		super.setRestOperations(restTemplate);
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 转为项目中的三方用户信息
		OAuth2ClientVO oauth2ThirdAccount = oauth2UserConverterContext.convert(userRequest, oAuth2User);
		// 检查用户信息
		oauth2ClientService.checkAndSaveUser(oauth2ThirdAccount);
		// 将loginType设置至attributes中
		LinkedHashMap<String, Object> attributes = new LinkedHashMap<>(oAuth2User.getAttributes());
		// 将yml配置的RegistrationId当做登录类型
		attributes.put("loginType", userRequest.getClientRegistration().getRegistrationId());
		String userNameAttributeName = userRequest.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();
		return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, userNameAttributeName);
	}
}