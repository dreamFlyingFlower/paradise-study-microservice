package com.wy.strategy;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.security.constant.ConstOAuthClient;

/**
 * 微信用户信息转换器
 *
 * @author 飞花梦影
 * @date 2024-11-04 17:05:39
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class WechatUserConverter implements OAuth2UserConverterStrategy {

	@Override
	public ThirdUserVO convert(OAuth2User oauth2User) {
		// 获取三方用户信息
		Map<String, Object> attributes = oauth2User.getAttributes();
		ThirdUserVO thirdAccount = new ThirdUserVO();
		thirdAccount.setUniqueId(String.valueOf(attributes.get("openid")));
		thirdAccount.setThirdUsername(oauth2User.getName());
		thirdAccount.setType(ConstOAuthClient.OAUTH2_CLIENT_LOGIN_WECHAT);
		thirdAccount.setLocation(attributes.get("province") + " " + attributes.get("city"));
		// 设置基础用户信息
		thirdAccount.setName(oauth2User.getName());
		thirdAccount.setAvatarUrl(String.valueOf(attributes.get("headimgurl")));
		return thirdAccount;
	}
}