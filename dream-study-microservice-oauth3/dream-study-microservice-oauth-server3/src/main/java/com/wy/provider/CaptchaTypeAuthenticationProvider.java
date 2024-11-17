package com.wy.provider;

import java.util.Objects;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.ObjectUtils;

import com.wy.constant.ConstAuthorizationServerRedis;
import com.wy.exception.AuthException;

import dream.flying.flower.autoconfigure.redis.helper.RedisStrHelpers;
import dream.flying.flower.framework.core.enums.LoginType;
import dream.flying.flower.framework.security.constant.ConstSecurity;
import dream.flying.flower.framework.web.helper.WebHelpers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证码校验.如注入该类的子类,会替换默认的DaoAuthenticationProvider
 *
 * @author 飞花梦影
 * @date 2024-09-18 22:19:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Setter
@Getter
public class CaptchaTypeAuthenticationProvider extends DaoAuthenticationProvider {

	protected String storeType;

	protected RedisStrHelpers redisStrHelpers;

	/**
	 * 注入UserDetailsService和passwordEncoder
	 *
	 * @param userDetailsService 用户服务,给框架提供用户信息
	 * @param passwordEncoder 密码解析器,用于加密和校验密码
	 */
	public CaptchaTypeAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		super.setPasswordEncoder(passwordEncoder);
		super.setUserDetailsService(userDetailsService);
	}

	/**
	 * 注入UserDetailsService和passwordEncoder
	 *
	 * @param userDetailsService 用户服务,给框架提供用户信息
	 * @param passwordEncoder 密码解析器,用于加密和校验密码
	 */
	public CaptchaTypeAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
			RedisStrHelpers redisStrHelpers) {
		this(userDetailsService, passwordEncoder);
		this.redisStrHelpers = redisStrHelpers;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		log.info("Authenticate captcha...");

		HttpServletRequest request = WebHelpers.getRequest();
		if (request == null) {
			throw new AuthException("Failed to get the current request.");
		}

		// 获取当前登录方式
		String loginType = request.getParameter(ConstSecurity.PARAMETER_NAME_LOGIN_TYPE);
		// 如果自定义的grant_type模式也需要校验图形验证码的可以不修改
		// if (!Objects.equals(loginType, ConstAuthorization.SMS_LOGIN_TYPE)) {
		// 只要不是密码登录都不需要校验图形验证码
		if (!Objects.equals(loginType, LoginType.PASSWORD.getValue() + "")) {
			log.info("It isn't necessary captcha authenticate.");
			return super.authenticate(authentication);
		}

		// 获取参数中的验证码
		String code = request.getParameter(OAuth2ParameterNames.CODE);
		if (ObjectUtils.isEmpty(code)) {
			throw new AuthException("The captcha cannot be empty.");
		}

		if ("session".equals(storeType)) {
			// 获取session中存储的验证码
			Object captchaCode =
					request.getSession(Boolean.FALSE).getAttribute(ConstSecurity.PARAMETER_NAME_CAPTCHA_ID);
			if (captchaCode instanceof String) {
				String sessionCode = (String) captchaCode;
				if (!sessionCode.equalsIgnoreCase(code)) {
					throw new AuthException("The captcha is incorrect.");
				}
			} else {
				throw new AuthException("The captcha is abnormal. Obtain it again.");
			}
		} else {
			String captchaId = request.getParameter(ConstSecurity.PARAMETER_NAME_CAPTCHA_ID);
			// 获取缓存中存储的验证码
			String captchaCode =
					redisStrHelpers.get(ConstAuthorizationServerRedis.IMAGE_CAPTCHA_PREFIX_KEY + captchaId);
			if (!ObjectUtils.isEmpty(captchaCode)) {
				if (!captchaCode.equalsIgnoreCase(code)) {
					throw new AuthException("The captcha is incorrect.");
				}
			} else {
				throw new AuthException("The captcha is abnormal. Obtain it again.");
			}

			// 删除缓存
			redisStrHelpers.delete(ConstAuthorizationServerRedis.IMAGE_CAPTCHA_PREFIX_KEY + captchaId);
		}

		log.info("Captcha authenticated.");
		return super.authenticate(authentication);
	}
}