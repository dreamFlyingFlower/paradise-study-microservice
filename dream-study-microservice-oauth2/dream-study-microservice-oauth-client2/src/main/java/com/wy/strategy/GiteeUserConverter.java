package com.wy.strategy;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.security.constant.ConstOAuthClient;

/**
 * 转换通过码云(gitee)登录的用户信息
 *
 * @author 飞花梦影
 * @date 2024-11-03 22:46:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class GiteeUserConverter implements OAuth2UserConverterStrategy {

	@Override
	public ThirdUserVO convert(OAuth2User oauth2User) {
		// 获取三方用户信息
		Map<String, Object> attributes = oauth2User.getAttributes();
		// 转换至OAuth2ClientVO
		ThirdUserVO oauthClientVo = new ThirdUserVO();
		oauthClientVo.setUniqueId(oauth2User.getName());
		oauthClientVo.setThirdUsername(String.valueOf(attributes.get("login")));
		oauthClientVo.setType(ConstOAuthClient.OAUTH2_CLIENT_LOGIN_GITEE);
		oauthClientVo.setBlog(String.valueOf(attributes.get("blog")));
		// 设置基础用户信息,比如用户名,真实姓名,头像等
		return oauthClientVo;
	}
}