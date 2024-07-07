package com.wy.social.qq;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 自定义SocialConfigurer,在将SocialAuthenticationFilter加入之前做一些自定义操作,需要重写postProcess()
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 09:41:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqSocialSecurityConfigurer extends SpringSocialConfigurer {

	private String filterProcessUrl;

	public QqSocialSecurityConfigurer(String filterProcessUrl) {
		this.filterProcessUrl = filterProcessUrl;
	}

	/**
	 * 重写需要放到过滤器链上的filter
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessUrl);
		return (T) filter;
	}
}