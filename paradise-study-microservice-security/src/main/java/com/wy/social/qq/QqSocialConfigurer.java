package com.wy.social.qq;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * @apiNote 自定义sociaconfigurer
 * @author ParadiseWY
 * @date 2019年9月26日
 */
public class QqSocialConfigurer extends SpringSocialConfigurer {

	private String filterProcessUrl;

	public QqSocialConfigurer(String filterProcessUrl) {
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
		return (T)filter;
	}
}