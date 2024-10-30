package com.wy.qrcode;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * QrcodeAuthentication认证,参照{@link DaoAuthenticationProvider}
 *
 * @author 飞花梦影
 * @date 2024-10-30 10:48:32
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Getter
@Setter
public class DefaultQrcodeAuthenticationProvider extends AbstractQrcodeAuthenticationProvider {

	/**
	 * The plaintext password used to perform
	 * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is not
	 * found to avoid SEC-2056.
	 */
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	private PasswordEncoder passwordEncoder;

	/**
	 * The password used to perform
	 * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is not
	 * found to avoid SEC-2056. This is necessary, because some
	 * {@link PasswordEncoder} implementations will short circuit if the password is
	 * not in a valid format.
	 */
	private volatile String userNotFoundEncodedPassword;

	private UserDetailsService userDetailsService;

	private UserDetailsPasswordService userDetailsPasswordService;

	public DefaultQrcodeAuthenticationProvider() {
		setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
	}

	@Override
	protected void doAfterPropertiesSet() {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	/**
	 * Allows subclasses to perform any additional checks of a returned (or cached)
	 * <code>UserDetails</code> for a given authentication request. Generally a
	 * subclass will at least compare the {@link Authentication#getCredentials()}
	 * with a {@link UserDetails#getPassword()}. If custom logic is needed to
	 * compare additional properties of <code>UserDetails</code> and/or
	 * <code>QrcodeAuthenticationToken</code>, these should also appear in this
	 * method.
	 * 
	 * @param userDetails as retrieved from the
	 *        {@link #retrieveUser(String, QrcodeAuthenticationToken)} or
	 *        <code>UserCache</code>
	 * @param authentication the current request that needs to be authenticated
	 * @throws AuthenticationException AuthenticationException if the credentials
	 *         could not be validated (generally a
	 *         <code>BadCredentialsException</code>, an
	 *         <code>AuthenticationServiceException</code>)
	 */
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, QrcodeAuthenticationToken authentication)
			throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			log.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(this.messages
					.getMessage("MyAbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		} else {
			String presentedPassword = authentication.getCredentials().toString();

			// 验证开始
			if ("phone".equals(authentication.getType())) {
				// 手机验证码验证，调用公共服务查询后台验证码缓存: key 为authentication.getPrincipal()的value,
				// 并判断其与验证码是否匹配,此处写死为 1000
				if (!"1000".equals(presentedPassword)) {
					log.debug("Authentication failed: verifyCode does not match stored value");
					throw new BadCredentialsException(this.messages.getMessage(
							"MyAbstractUserDetailsAuthenticationProvider.badCredentials", "Bad verifyCode"));
				}
			} else if (QrcodeLoginAuthenticationFilter.LOGIN_RESTFUL_TYPE_QR.equals(authentication.getType())) {
				// 二维码只需要根据 qrCode 查询到用户即可,所以此处无需验证
			} else {
				// 用户名密码验证
				if (!this.passwordEncoder.matches(userDetails.getPassword(), presentedPassword)) {
					log.debug("Authentication failed: password does not match stored value");
					throw new BadCredentialsException(this.messages.getMessage(
							"MyAbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
				}
			}
		}
	}

	/**
	 * Allows subclasses to actually retrieve the <code>UserDetails</code> from an
	 * implementation-specific location, with the option of throwing an
	 * <code>AuthenticationException</code> immediately if the presented credentials
	 * are incorrect (this is especially useful if it is necessary to bind to a
	 * resource as the user in order to obtain or generate a
	 * <code>UserDetails</code>).
	 * <p>
	 * Subclasses are not required to perform any caching, as the
	 * <code>AbstractUserDetailsAuthenticationProvider</code> will by default cache
	 * the <code>UserDetails</code>. The caching of <code>UserDetails</code> does
	 * present additional complexity as this means subsequent requests that rely on
	 * the cache will need to still have their credentials validated, even if the
	 * correctness of credentials was assured by subclasses adopting a binding-based
	 * strategy in this method. Accordingly it is important that subclasses either
	 * disable caching (if they want to ensure that this method is the only method
	 * that is capable of authenticating a request, as no <code>UserDetails</code>
	 * will ever be cached) or ensure subclasses implement
	 * {@link #additionalAuthenticationChecks(UserDetails, QrcodeAuthenticationToken)}
	 * to compare the credentials of a cached <code>UserDetails</code> with
	 * subsequent authentication requests.
	 * </p>
	 * <p>
	 * Most of the time subclasses will not perform credentials inspection in this
	 * method, instead performing it in
	 * {@link #additionalAuthenticationChecks(UserDetails, QrcodeAuthenticationToken)}
	 * so that code related to credentials validation need not be duplicated across
	 * two methods.
	 * </p>
	 * 
	 * @param username The username to retrieve
	 * @param authentication The authentication request, which subclasses
	 *        <em>may</em> need to perform a binding-based retrieval of the
	 *        <code>UserDetails</code>
	 * @return the user information (never <code>null</code> - instead an exception
	 *         should the thrown)
	 * @throws AuthenticationException if the credentials could not be validated
	 *         (generally a <code>BadCredentialsException</code>, an
	 *         <code>AuthenticationServiceException</code> or
	 *         <code>UsernameNotFoundException</code>)
	 */
	@Override
	protected UserDetails retrieveUser(String username, QrcodeAuthenticationToken authentication)
			throws AuthenticationException {
		prepareTimingAttackProtection();
		UserDetails loadedUser;
		try {
			// 调用loadUserByUsername时加入type前缀
			loadedUser = this.getUserDetailsService().loadUserByUsername(authentication.getType() + ":" + username);
		} catch (UsernameNotFoundException e) {
			mitigateAgainstTimingAttack(authentication);
			throw e;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		} else {
			return loadedUser;
		}
	}

	private void prepareTimingAttackProtection() {
		if (this.userNotFoundEncodedPassword == null) {
			this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
		}
	}

	private void mitigateAgainstTimingAttack(QrcodeAuthenticationToken authentication) {
		if (authentication.getCredentials() != null) {
			String presentedPassword = authentication.getCredentials().toString();
			this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
		}
	}

	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		boolean upgradeEncoding =
				this.userDetailsPasswordService != null && this.passwordEncoder.upgradeEncoding(user.getPassword());
		if (upgradeEncoding) {
			String presentedPassword = authentication.getCredentials().toString();
			String newPassword = this.passwordEncoder.encode(presentedPassword);
			user = this.userDetailsPasswordService.updatePassword(user, newPassword);
		}
		return super.createSuccessAuthentication(principal, authentication, user);
	}
}