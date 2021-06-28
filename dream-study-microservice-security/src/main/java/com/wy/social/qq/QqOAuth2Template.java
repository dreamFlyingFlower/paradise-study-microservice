package com.wy.social.qq;

import java.nio.charset.Charset;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 自定义OAuth2Template,重写创建restTemplate的方法,添加自定义的请求方式
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 12:29:52
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqOAuth2Template extends OAuth2Template {

	public QqOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		/**
		 * 将useParametersForClientAuthentication设为true,可自动传递client_id和client_secret.也可重写exchangeForAccess()
		 */
		setUseParametersForClientAuthentication(true);
	}

	/**
	 * 重写本方服务器接收授权服务器返回的请求头类型.默认是表单,文件上传,json,但是缺少text/plain,需自定添加
	 */
	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}

	/**
	 * 重写restTemplate解析授权服务器返回的信息的方式,默认授权服务器返回的是json格式
	 * 若授权服务器返回的结果是json,则不需要重写,若不是json,则需要根据授权服务器返回的方式进行解析
	 * 
	 * {@link OAuth2Template#extractAccessGrant}:授权服务器默认返回access_token的结果中含有以下参数:<br>
	 * access_token,scope,refresh_token,expires_in
	 */
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		// 若返回的是字符串,假设是利用某分隔符进行分割的
		String result = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
		String[] tokens = result.split("&");
		return new AccessGrant(tokens[0], tokens[1], tokens[2], Long.parseLong(tokens[3]));
	}
}