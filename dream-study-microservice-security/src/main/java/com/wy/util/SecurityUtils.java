package com.wy.util;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wy.collection.ListHelper;
import com.wy.common.AuthException;
import com.wy.entity.User;
import com.wy.enums.TipEnum;
import com.wy.lang.StrHelper;

/**
 * 安全服务工具类
 * 
 * @auther 飞花梦影
 * @date 2021-06-29 00:08:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class SecurityUtils {

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
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,
				getAuthentication().getCredentials(), getAuthentication().getAuthorities()));
	}

	/**
	 * 修改了用户密码之后 重新存储security中用户密码
	 * 
	 * @param password 新的密码
	 */
	public static void setLoginPwd(String password) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				getAuthentication().getPrincipal(), password, getAuthentication().getAuthorities()));
	}

	/**
	 * 修改了用户权限之后,重新存储security中用户权限
	 * 
	 * @param authorities 新的用户权限
	 */
	public static void setLoginAuthorities(Collection<? extends GrantedAuthority> authorities) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				getAuthentication().getPrincipal(), getAuthentication().getCredentials(), authorities));
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
}