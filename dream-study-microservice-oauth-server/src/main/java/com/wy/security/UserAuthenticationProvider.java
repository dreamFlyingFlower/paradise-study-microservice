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
import org.springframework.stereotype.Component;

import com.wy.entity.User;
import com.wy.service.UserService;

import dream.flying.flower.enums.TipEnum;

/**
 * 自定义用户名和密码的登录方法,从数据库中取出用户数据封装后返回
 * 
 * @author 飞花梦影
 * @date 2021-07-06 15:48:34
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 对前端传递的认证参数进行处理
	 * 
	 * @param authentication 必须是UsernamePasswordAuthenticationToken或其子类
	 * @return 一般和参数authentication是同类型
	 * @throws AuthenticationException
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 获取表单输入中返回的用户名,用户名必须唯一
		String username = authentication.getName();
		// 获取表单中输入的密码
		String password = (String) authentication.getCredentials();
		// 调用自定义方法获取用户数据,判断用户是否存在和密码是否正确
		User user = (User) userService.loadUserByUsername(username);
		if (user == null) {
			throw new BadCredentialsException(TipEnum.TIP_LOGIN_FAIL_USERNAME.getMsg());
		}
		// 使用BCryptPasswordEncoder加密,加密后的长度为60,且被加密的字符串不得超过72
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException(TipEnum.TIP_LOGIN_FAIL_PASSWORD.getMsg());
		}
		// 添加一些其他账号信息的判断,如用户权限,用户账号是否已停用等
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		UsernamePasswordAuthenticationToken result =
				new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), authorities);
		result.setDetails(authentication.getDetails());
		// 构建返回的用户登录成功的token
		return result;
	}

	/**
	 * 判断传入的authentication是否是UsernamePasswordAuthenticationToken或其子类,若不是,不走当前provider
	 * 
	 * @param authentication {@link Authentication}实现类
	 * @return true->是UsernamePasswordAuthenticationToken或其子类;false->不是
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}