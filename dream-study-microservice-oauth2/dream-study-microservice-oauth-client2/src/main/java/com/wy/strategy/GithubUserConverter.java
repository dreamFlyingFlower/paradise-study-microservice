package com.wy.strategy;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.security.constant.ConstOAuthClient;
import lombok.RequiredArgsConstructor;

/**
 * 转换通过github登录的用户信息
 *
 * @author 飞花梦影
 * @date 2024-11-03 22:46:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
@Component
public class GithubUserConverter implements OAuth2UserConverterStrategy {

	private final GiteeUserConverter giteeUserConverter;

	@Override
	public ThirdUserVO convert(OAuth2User oauth2User) {
		// github与gitee目前所取字段一致,直接调用gitee的解析
		ThirdUserVO oauthClientVo = giteeUserConverter.convert(oauth2User);
		// 提取location
		Object location = oauth2User.getAttributes().get("location");
		oauthClientVo.setLocation(String.valueOf(location));
		// 设置登录类型
		oauthClientVo.setType(ConstOAuthClient.OAUTH2_CLIENT_LOGIN_GITHUB);
		return oauthClientVo;
	}
}