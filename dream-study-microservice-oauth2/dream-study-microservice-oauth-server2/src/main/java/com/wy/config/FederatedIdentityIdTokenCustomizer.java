package com.wy.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import dream.flying.flower.framework.security.constant.ConstSecurity;

/**
 * 编写联合身份认证自定义token处理,当使用openId Connect登录时将用户信息写入idToken中
 *
 * @author 飞花梦影
 * @date 2024-11-04 15:15:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class FederatedIdentityIdTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

	private static final Set<String> ID_TOKEN_CLAIMS = new HashSet<>(
			Arrays.asList(IdTokenClaimNames.ISS, IdTokenClaimNames.SUB, IdTokenClaimNames.AUD, IdTokenClaimNames.EXP,
					IdTokenClaimNames.IAT, IdTokenClaimNames.AUTH_TIME, IdTokenClaimNames.NONCE, IdTokenClaimNames.ACR,
					IdTokenClaimNames.AMR, IdTokenClaimNames.AZP, IdTokenClaimNames.AT_HASH, IdTokenClaimNames.C_HASH));

	@Override
	public void customize(JwtEncodingContext context) {
		if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
			Map<String, Object> thirdPartyClaims = extractClaims(context.getPrincipal());
			context.getClaims().claims(existingClaims -> {
				// Remove conflicting claims set by this authorization server
				existingClaims.keySet().forEach(thirdPartyClaims::remove);

				// Remove standard id_token claims that could cause problems with clients
				ID_TOKEN_CLAIMS.forEach(thirdPartyClaims::remove);

				// Add all other claims directly to id_token
				existingClaims.putAll(thirdPartyClaims);
			});
		}

		// 检查登录用户信息是不是OAuth2User,在token中添加loginType属性
		if (context.getPrincipal().getPrincipal() instanceof OAuth2User) {
			OAuth2User user = (OAuth2User) context.getPrincipal().getPrincipal();
			JwtClaimsSet.Builder claims = context.getClaims();
			Object loginType = user.getAttribute("loginType");
			if (loginType instanceof String) {
				// 同时检验是否为String和是否不为空
				claims.claim("loginType", loginType);
			}
		}

		// 检查登录用户信息是不是UserDetails,排除掉没有用户参与的流程
		if (context.getPrincipal().getPrincipal() instanceof UserDetails) {
			UserDetails user = (UserDetails) context.getPrincipal().getPrincipal();
			// 获取申请的scopes
			Set<String> scopes = context.getAuthorizedScopes();
			// 获取用户的权限
			Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
			// 提取权限并转为字符串
			Set<String> authoritySet = Optional.ofNullable(authorities)
					.orElse(Collections.emptyList())
					.stream()
					// 获取权限字符串
					.map(GrantedAuthority::getAuthority)
					// 去重
					.collect(Collectors.toSet());

			// 合并scope与用户信息
			authoritySet.addAll(scopes);

			JwtClaimsSet.Builder claims = context.getClaims();
			// 将权限信息放入jwt的claims中（也可以生成一个以指定字符分割的字符串放入）
			claims.claim(ConstSecurity.AUTHORITIES_KEY, authoritySet);
			// 放入其它自定内容
			// 角色、头像...
		}
	}

	private Map<String, Object> extractClaims(Authentication principal) {
		Map<String, Object> claims;
		if (principal.getPrincipal() instanceof OidcUser) {
			OidcUser oidcUser = (OidcUser) principal.getPrincipal();
			OidcIdToken idToken = oidcUser.getIdToken();
			claims = idToken.getClaims();
		} else if (principal.getPrincipal() instanceof OAuth2User) {
			OAuth2User oauth2User = (OAuth2User) principal.getPrincipal();
			claims = oauth2User.getAttributes();
		} else {
			claims = Collections.emptyMap();
		}

		return new HashMap<>(claims);
	}
}