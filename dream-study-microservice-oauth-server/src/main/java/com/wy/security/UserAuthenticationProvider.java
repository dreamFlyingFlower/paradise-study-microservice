package com.wy.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wy.entity.User;
import com.wy.service.UserService;

/**
 * 自定义登录的验证方法,从数据库中取出用户数据封装后返回
 * 
 * @author 飞花梦影
 * @date 2021-07-06 15:48:34
 * @git {@link https://github.com/dreamFlyingFlower }
 */
// @Configuration
public class UserAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 获取表单输入中返回的用户名,用户名必须唯一
		String username = authentication.getName();
		// 获取表单中输入的密码
		String password = (String) authentication.getCredentials();
		// 调用自定义方法获取用户数据,判断用户是否存在和密码是否正确
		User user = (User) userService.loadUserByUsername(username);
		if (user == null) {
			throw new BadCredentialsException("用户名不存在");
		}
		// 使用BCryptPasswordEncoder加密,加密后的长度为60,且被加密的字符串不得超过72
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("密码不正确");
		}
		// 添加一些其他账号信息的判断,如用户权限,用户账号是否已停用等
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		// 构建返回的用户登录成功的token
		return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}