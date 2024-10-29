package com.wy.oauth.grant;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义手机号认证对象,需实现{@link Authentication}或其子类
 * 
 * {@link Authentication}:认证对象
 * 
 * <pre>
 * principal:认证主体,通常是用户对象{@link UserDetails}或其实现类
 * credentials:存储了与主体关联的认证信息,例如密码
 * authorities:主体所拥有的权限集合
 * authenticated:是否已经通过认证,true为已认证,false为未认证
 * details:用于存储与认证令牌相关的附加信息.eg,在基于表单的认证中,可以将表单提交的用户名和密码存储在credentials属性中,并将其他与认证相关的详细信息存储在details中
 * </pre>
 * 
 * {@link AbstractAuthenticationToken}:抽象认证对象,实现了部分方法
 *
 * @author 飞花梦影
 * @date 2024-10-24 14:45:41
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class PhoneAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 2530206941077115912L;

	private final Object principal;

	private final Object credentials;

	/**
	 * 自定义认证需要的属性
	 */
	private String phone;

	/**
	 * 创建一个未认证的对象
	 * 
	 * @param principal
	 * @param credentials
	 */
	public PhoneAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(false);
	}

	/**
	 * 创建一个已认证对象
	 * 
	 * @param authorities
	 * @param principal
	 * @param credentials
	 */
	public PhoneAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal,
			Object credentials) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		// 必须使用super,因为要重写
		super.setAuthenticated(true);
	}

	/**
	 * 不能暴露Authenticated的设置方法,防止直接设置
	 * 
	 * @param isAuthenticated
	 * @throws IllegalArgumentException
	 */
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		Assert.isTrue(!isAuthenticated,
				"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		super.setAuthenticated(false);
	}

	/**
	 * 被认证主体的身份,如果是用户名/密码登录,就是用户名
	 * 
	 * @return Object
	 */
	@Override
	public Object getPrincipal() {
		return principal;
	}

	/**
	 * 用户凭证,如密码
	 * 
	 * @return Object
	 */
	@Override
	public Object getCredentials() {
		return credentials;
	}
}