package com.wy.oauth.grant;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 实现自定义的用户加载服务
 *
 * @author 飞花梦影
 * @date 2024-10-24 15:08:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class PhoneUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 此处的username为phone,phoneCode可以通过手机号从缓存或其他方式去验证
		return null;
	}
}