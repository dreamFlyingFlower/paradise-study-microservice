package com.wy.social.qq;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @apiNote
 * @author ParadiseWY
 * @date 2019年9月26日
 */
public class QqTemplate extends OAuth2Template {

	public QqTemplate(String clientId, String clientSecret, String authorizeUrl,
			String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		/**
		 * 在获得授权码之后再向授权服务器获得accesstoken的时候,需要传递client_id和client_secret参数
		 * 详见OAuth2Template#exchangeForAccess方法,但是必须是useParametersForClientAuthentication为true
		 * 若不重写exchangeForAccess方法,则需要将useParametersForClientAuthentication设为true
		 */
		setUseParametersForClientAuthentication(true);
	}

	/**
	 * 重写本方服务器接收授权服务器返回的请求头类型
	 */
	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		restTemplate.getMessageConverters()
				.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}

	/**
	 * @apiNote 重写resttemplate解析授权服务器返回的信息的方式,默认授权服务器返回的是json格式
	 *          若授权服务器返回的结果是json,则不需要重写,若不是json,则需要根据授权服务器返回的方式进行解析
	 * @apiNote 默认授权服务器返回的accesstoken结果中含有以下参数:access_token,scope,refresh_token,expires_in
	 *          可从postForAccessGrant的源码中查看
	 */
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl,
			MultiValueMap<String, String> parameters) {
		// 若返回的是字符串,假设是利用某分隔符进行分割的
		String result = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
		String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(result, "&");
		return new AccessGrant(tokens[0], tokens[1], tokens[2], Long.parseLong(tokens[3]));
	}
}