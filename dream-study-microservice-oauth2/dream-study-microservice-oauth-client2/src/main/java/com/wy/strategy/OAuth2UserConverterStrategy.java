package com.wy.strategy;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.wy.vo.OAuth2ClientVO;

/**
 * 使用策略模式编写一套用户信息转换类,将三方用户信息转成自己系统里的用户信息并储存
 *
 * @author 飞花梦影
 * @date 2024-11-03 22:44:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface OAuth2UserConverterStrategy {

	/**
	 * 将OAuth2登录的认证信息转为 {@link OAuth2ClientVO}
	 *
	 * @param oauth2User oauth2登录获取的用户信息
	 * @return 本系统中的用户信息
	 */
	OAuth2ClientVO convert(OAuth2User oauth2User);
}