package com.wy.util;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.wy.common.AuthException;
import com.wy.entity.User;

import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.enums.TipEnum;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.lang.StrHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 安全服务工具类
 * 
 * @auther 飞花梦影
 * @date 2021-06-29 00:08:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class SecurityHelpers {

	/**
	 * 获取Authentication认证信息
	 */
	public static Authentication getAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.isAuthenticated()) {
			throw new AuthException(TipEnum.TIP_LOGIN_FAIL_NOT_LOGIN);
		}
		return authentication;
	}

	/**
	 * 认证与鉴权失败回调
	 *
	 * @param request 当前请求
	 * @param response 当前响应
	 * @param e 具体的异常信息
	 */
	public static void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable e) {
		Map<String, String> parameters = getErrorParameter(request, response, e);
		String wwwAuthenticate = computeWwwAuthenticateHeaderValue(parameters);
		response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
		try {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(JsonHelpers.toString(parameters));
			response.getWriter().flush();
		} catch (IOException ex) {
			log.error("写回错误信息失败", e);
		}
	}

	/**
	 * 获取异常信息map
	 *
	 * @param request 当前请求
	 * @param response 当前响应
	 * @param e 本次异常具体的异常实例
	 * @return 异常信息map
	 */
	private static Map<String, String> getErrorParameter(HttpServletRequest request, HttpServletResponse response,
			Throwable e) {
		Map<String, String> parameters = new LinkedHashMap<>();
		if (request.getUserPrincipal() instanceof AbstractOAuth2TokenAuthenticationToken) {
			// 权限不足
			parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
			parameters.put("error_description",
					"The request requires higher privileges than provided by the access token.");
			parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
			response.setStatus(HttpStatus.FORBIDDEN.value());
		}
		if (e instanceof OAuth2AuthenticationException authenticationException) {
			// jwt异常，e.g. jwt超过有效期、jwt无效等
			OAuth2Error error = authenticationException.getError();
			parameters.put("error", error.getErrorCode());
			if (StrHelper.isNotBlank(error.getUri())) {
				parameters.put("error_uri", error.getUri());
			}
			if (StrHelper.isNotBlank(error.getDescription())) {
				parameters.put("error_description", error.getDescription());
			}
			if (error instanceof BearerTokenError bearerTokenError) {
				if (StrHelper.isNotBlank(bearerTokenError.getScope())) {
					parameters.put("scope", bearerTokenError.getScope());
				}
				response.setStatus(bearerTokenError.getHttpStatus().value());
			}
		}
		if (e instanceof InsufficientAuthenticationException) {
			// 没有携带jwt访问接口，没有客户端认证信息
			parameters.put("error", BearerTokenErrorCodes.INVALID_TOKEN);
			parameters.put("error_description", "Not authorized.");
			parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
		parameters.put("message", e.getMessage());
		return parameters;
	}

	/**
	 * 生成放入请求头的错误信息
	 *
	 * @param parameters 参数
	 * @return 字符串
	 */
	public static String computeWwwAuthenticateHeaderValue(Map<String, String> parameters) {
		StringBuilder wwwAuthenticate = new StringBuilder();
		wwwAuthenticate.append("Bearer");
		if (!parameters.isEmpty()) {
			wwwAuthenticate.append(" ");
			int i = 0;
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
				if (i != parameters.size() - 1) {
					wwwAuthenticate.append(", ");
				}
				i++;
			}
		}
		return wwwAuthenticate.toString();
	}

	/**
	 * 生成BCryptPasswordEncoder密码,每次加密都不同,加密后的长度为60,且被加密的字符串不得超过72
	 * 
	 * @param password 密码
	 * @return 加密字符串
	 */
	public static String encode(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}

	/**
	 * 判断密码是否相同
	 * 
	 * @param originlPwd 真实密码,未加密
	 * @param encodedPwd 加密后字符
	 * @return 结果
	 */
	public static boolean matches(String originlPwd, String encodedPwd) {
		return new BCryptPasswordEncoder().matches(originlPwd, encodedPwd);
	}

	/**
	 * 获取用户,需要和登录时存入缓存的对象相同 {@link LoginAuthenticationProvider#authenticate}
	 */
	public static User getLoginUser() {
		return (User) getAuthentication().getPrincipal();
	}

	/**
	 * 修改了用户信息之后 重新存储security中用户信息
	 * 
	 * @param user 新的用户信息
	 */
	public static void setLoginUser(User user) {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(user, getAuthentication().getCredentials(),
						getAuthentication().getAuthorities()));
	}

	/**
	 * 修改了用户密码之后 重新存储security中用户密码
	 * 
	 * @param password 新的密码
	 */
	public static void setLoginPwd(String password) {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(getAuthentication().getPrincipal(), password,
						getAuthentication().getAuthorities()));
	}

	/**
	 * 修改了用户权限之后,重新存储security中用户权限
	 * 
	 * @param authorities 新的用户权限
	 */
	public static void setLoginAuthorities(Collection<? extends GrantedAuthority> authorities) {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(getAuthentication().getPrincipal(),
						getAuthentication().getCredentials(), authorities));
	}

	/**
	 * 修改用户信息之后,重新存储security中的用户信息
	 * 
	 * @param user 用户信息,若为null,则使用原来的用户信息
	 * @param password 用户密码,若为null,则使用原来的用户密码
	 * @param authorities 用户权限,若为null,则使用原来的用户权限
	 */
	public static void setLoginUser(User user, String password, Collection<? extends GrantedAuthority> authorities) {
		User newUser = Objects.isNull(user) ? getLoginUser() : user;
		Object newPwd = StrHelper.isBlank(password) ? getAuthentication().getCredentials() : password;
		Collection<? extends GrantedAuthority> newAuthorities =
				ListHelper.isEmpty(authorities) ? getAuthentication().getAuthorities() : authorities;
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(newUser, newPwd, newAuthorities));
	}

	/**
	 * 提取请求中的参数并转为一个map返回
	 *
	 * @param request 当前请求
	 * @return 请求中的参数
	 */
	public static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
		parameterMap.forEach((key, values) -> {
			if (values.length > 0) {
				for (String value : values) {
					parameters.add(key, value);
				}
			}
		});
		return parameters;
	}

	/**
	 * 从认证信息中获取客户端token
	 *
	 * @param authentication 认证信息
	 * @return 客户端认证信息，获取失败抛出异常
	 */
	public static OAuth2ClientAuthenticationToken
			getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
		OAuth2ClientAuthenticationToken clientPrincipal = null;
		if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
		}
		if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
			return clientPrincipal;
		}
		throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
	}

	/**
	 * 抛出 OAuth2AuthenticationException 异常
	 *
	 * @param errorCode 错误码
	 * @param message 错误信息
	 * @param errorUri 错误对照地址
	 */
	public static void throwError(String errorCode, String message, String errorUri) {
		OAuth2Error error = new OAuth2Error(errorCode, message, errorUri);
		throw new OAuth2AuthenticationException(error);
	}
}