package com.wy.token;

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
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import dream.flying.flower.framework.security.constant.ConstSecurity;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义JWT,IdToken,将权限以及其他通用信息放至其中
 *
 * @author 飞花梦影
 * @date 2024-11-04 15:15:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class CustomizerTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

	private static final Set<String> ID_TOKEN_CLAIMS = new HashSet<>(Arrays.asList(
			// 令牌颁发者
			IdTokenClaimNames.ISS,
			// 用户id
			IdTokenClaimNames.SUB,
			// 令牌接收者,OAuth应用ID
			IdTokenClaimNames.AUD,
			// 令牌过期时间戳
			IdTokenClaimNames.EXP,
			// 令牌颁发时间戳
			IdTokenClaimNames.IAT, IdTokenClaimNames.AUTH_TIME,
			// 随机字符串,用来防止重放攻击
			IdTokenClaimNames.NONCE,
			// 身份验证上下文类引用
			IdTokenClaimNames.ACR,
			// 身份验证方法参考
			IdTokenClaimNames.AMR,
			// ID令牌发放给的授权方
			IdTokenClaimNames.AZP,
			// 访问令牌哈希值
			IdTokenClaimNames.AT_HASH,
			// 授权码哈希值
			IdTokenClaimNames.C_HASH));

	@Override
	public void customize(JwtEncodingContext context) {
		// 根据token类型添加信息
		OAuth2TokenType tokenType = context.getTokenType();
		if (log.isDebugEnabled()) {
			log.debug("客户端{}当前认证类型为:{}", context.getRegisteredClient().getClientId(), tokenType.getValue());
		}

		if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
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

		if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
			context.getClaims().claim("Test", "Test Access Token");
		}

		// 检查登录用户信息是不是OAuth2User,在token中添加loginType属性
		if (context.getPrincipal().getPrincipal() instanceof OAuth2User) {
			OAuth2User oauth2User = (OAuth2User) context.getPrincipal().getPrincipal();
			JwtClaimsSet.Builder claims = context.getClaims();
			Object loginType = oauth2User.getAttribute(ConstSecurity.OAUTH_LOGIN_TYPE);
			// 同时检验是否为String和是否不为空
			claims.claim(ConstSecurity.OAUTH_LOGIN_TYPE, loginType);
		}

		// 检查登录用户信息是不是UserDetails,排除掉没有用户参与的流程
		if (context.getPrincipal().getPrincipal() instanceof UserDetails) {
			UserDetails user = (UserDetails) context.getPrincipal().getPrincipal();
			// 获取用户权限
			Set<String> authoritySet = transferToContext(user.getAuthorities(), context);

			JwtClaimsSet.Builder claims = context.getClaims();
			// 将权限信息放入jwt的claims中(也可以生成一个以指定字符分割的字符串放入)
			claims.claim(ConstSecurity.AUTHORITIES_KEY, authoritySet);
			// 放入其它自定义内容,不能存放Long和Integer类型,否则无法反序列化
			// 具体可使用类型见JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper.objectMapper
			claims.claim("username", user.getUsername());
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

	private Set<String> transferToContext(Collection<? extends GrantedAuthority> authorities,
			JwtEncodingContext context) {
		// 获取申请的scopes
		Set<String> scopes = context.getAuthorizedScopes();
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
		return authoritySet;
	}
}