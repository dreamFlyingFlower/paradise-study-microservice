package com.wy.grant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

/**
 * 手机验证码认证授权提供者
 * 
 * 因为身份认证是由AuthenticationProvider实现的,所以还需要实现一个自定义AuthenticationProvider.
 * 
 * 如果AuthenticationProvider认证成功,会返回一个Authentication,其中authenticated属性为true,已授权的权限列表(GrantedAuthority列表),以及用户凭证.认证失败抛异常
 *
 * @author 飞花梦影
 * @date 2024-10-24 15:04:55
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
public class PhoneAuthenticationProvider implements AuthenticationProvider {

	private RedisTemplate<String, Object> redisTemplate;

	private PhoneUserDetailsService phoneUserDetailsService;

	public static final String PHONE_CODE_SUFFIX = "phone:code:";

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		PhoneAuthenticationToken authenticationToken = (PhoneAuthenticationToken) authentication;
		// 手机号
		Object principal = authenticationToken.getPrincipal();
		// 验证码
		Object credentials = authenticationToken.getCredentials();
		if (principal == null || "".equals(principal.toString()) || credentials == null
				|| "".equals(credentials.toString())) {
			throw new InternalAuthenticationServiceException("手机/手机验证码为空！");
		}
		// 获取手机号和验证码
		String phone = (String) principal;
		String phoneCode = (String) credentials;
		// 查找手机用户信息,验证用户是否存在
		UserDetails userDetails = phoneUserDetailsService.loadUserByUsername(phone);
		if (userDetails == null) {
			throw new InternalAuthenticationServiceException("用户手机不存在！");
		}
		String codeKey = PHONE_CODE_SUFFIX + phone;
		// 手机用户存在,验证手机验证码是否正确
		if (!redisTemplate.hasKey(codeKey)) {
			throw new InternalAuthenticationServiceException("验证码不存在或已失效！");
		}
		String realCode = (String) redisTemplate.opsForValue().get(codeKey);
		if (StringUtils.isBlank(realCode) || !realCode.equals(phoneCode)) {
			throw new InternalAuthenticationServiceException("验证码错误！");
		}
		// 返回认证成功的对象
		PhoneAuthenticationToken phoneAuthenticationToken =
				new PhoneAuthenticationToken(userDetails.getAuthorities(), phone, phoneCode);
		phoneAuthenticationToken.setPhone(phone);
		phoneAuthenticationToken.setDetails(userDetails);
		return phoneAuthenticationToken;
	}

	/**
	 * ProviderManager选择具体Provider时根据此方法判断authentication是不是PhoneAuthenticationToken的子类或子接口
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		// isAssignableFrom方法如果比较类authentication和被比较类类型相同,或者是其子类、实现类,返回true
		return PhoneAuthenticationToken.class.isAssignableFrom(authentication);
	}
}