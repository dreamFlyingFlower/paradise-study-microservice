package com.wy.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wy.entity.User;
import com.wy.service.UserService;

/**
 * 自定义登录的验证方法.可实现{@link AuthenticationProvider}或继承{@link AbstractUserDetailsAuthenticationProvider}
 * 
 * 若需要登录验证,则请求的url中必须携带验证参数verifyType,该参数的值必须和需要验证的参数key相同,
 * 如verifyType=image,则需要验证的值的key必须是image,如image=需要验证的值
 *
 * @author 飞花梦影
 * @date 2022-05-18 23:49:28
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
public class UserAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 这个获取表单输入中返回的用户名,用户名必须唯一
		String username = authentication.getName();
		// 这个是表单中输入的密码
		String password = (String) authentication.getCredentials();
		// 这里调用自己写的获取用户的方法来判断用户是否存在和密码是否正确
		User user = (User) userService.loadUserByUsername(username);
		if (user == null) {
			throw new BadCredentialsException("用户名不存在");
		}
		// 使用该加密方式是spring推荐,加密后的长度为60,且被加密的字符串不得超过72
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("密码不正确");
		}
		// 这里还可以加一些其他信息的判断,比如用户账号已停用等判断,这里为了方便我接下去的判断
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		// 构建返回的用户登录成功的token
		return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	/**
	 * 返回true表示支持这个执行
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}