package com.wy.provider.sms;

import java.util.Objects;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

import com.wy.constant.ConstAuthorizationServerRedis;
import com.wy.exception.AuthException;
import com.wy.provider.CaptchaTypeAuthenticationProvider;

import dream.flying.flower.autoconfigure.redis.helper.RedisStrHelpers;
import dream.flying.flower.framework.constant.enums.LoginPasswordType;
import dream.flying.flower.framework.security.constant.ConstOAuthGrantType;
import dream.flying.flower.framework.security.constant.ConstSecurity;
import dream.flying.flower.framework.web.WebHelpers;
import dream.flying.flower.lang.StrHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信验证码校验实现
 *
 * @author 飞花梦影
 * @date 2024-09-18 22:19:07
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
public class SmsTypeAuthenticationProvider extends CaptchaTypeAuthenticationProvider {

	/**
	 * 注入UserDetailsService和passwordEncoder
	 *
	 * @param userDetailsService 用户服务,给框架提供用户信息
	 * @param passwordEncoder 密码解析器,用于加密和校验密码
	 */
	public SmsTypeAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
			RedisStrHelpers redisStrHelpers) {
		super(userDetailsService, passwordEncoder, redisStrHelpers);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		log.info("Authenticate sms captcha...");

		if (authentication.getCredentials() == null) {
			this.logger.debug("Failed to authenticate since no credentials provided");
			throw new BadCredentialsException("The sms captcha cannot be empty.");
		}

		// 获取当前request
		HttpServletRequest request = WebHelpers.getRequest();
		if (request == null) {
			throw new AuthException("Failed to get the current request.");
		}

		// 获取当前登录方式
		String loginType = request.getParameter(ConstSecurity.PARAMETER_NAME_LOGIN_TYPE);
		// 获取grant_type
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		// 如果是自定义密码模式则下方的认证判断只要判断下loginType即可
		// if (Objects.equals(loginType, SecurityConstants.SMS_LOGIN_TYPE)) {}
		// 短信登录和自定义短信认证grant_type会走下方认证
		if (StrHelper.equalsIgnoreCase(loginType, LoginPasswordType.NONE.getValue() + "")
				|| StrHelper.equalsIgnoreCase(grantType, ConstOAuthGrantType.SMS_CODE.getValue())) {
			if ("session".equals(storeType)) {
				// 获取存入session的验证码(UsernamePasswordAuthenticationToken的principal中现在存入的是手机号)
				String smsCaptcha =
						(String) request.getSession(Boolean.FALSE).getAttribute((String) authentication.getPrincipal());
				// 校验输入的验证码是否正确(UsernamePasswordAuthenticationToken的credentials中现在存入的是输入的验证码)
				if (!Objects.equals(smsCaptcha, authentication.getCredentials())) {
					throw new BadCredentialsException("The sms captcha is incorrect.");
				}
			} else {
				// 获取存入缓存中的验证码(UsernamePasswordAuthenticationToken的principal中现在存入的是手机号)
				String smsCaptcha = redisStrHelpers
						.get(ConstAuthorizationServerRedis.SMS_CAPTCHA_PREFIX_KEY + authentication.getPrincipal());
				// 校验输入的验证码是否正确(UsernamePasswordAuthenticationToken的credentials中现在存入的是输入的验证码)
				if (!Objects.equals(smsCaptcha, authentication.getCredentials())) {
					throw new BadCredentialsException("The sms captcha is incorrect.");
				}
				// 删除缓存
				redisStrHelpers
						.delete((ConstAuthorizationServerRedis.SMS_CAPTCHA_PREFIX_KEY + authentication.getPrincipal()));
			}
			// 在这里也可以拓展其它登录方式,比如邮箱登录什么的
		} else {
			log.info("Not sms captcha loginType, exit.");
			// 其它调用父类默认实现的密码方式登录
			super.additionalAuthenticationChecks(userDetails, authentication);
		}

		log.info("Authenticated sms captcha.");
	}
}