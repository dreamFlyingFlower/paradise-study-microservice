package com.wy.provider.qrcode;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * QrcodeAuthentication认证,参照{@link AbstractUserDetailsAuthenticationProvider}
 *
 * @author 飞花梦影
 * @date 2024-10-30 10:48:32
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractQrcodeAuthenticationProvider
		implements AuthenticationProvider, InitializingBean, MessageSourceAware {

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	private UserCache userCache = new NullUserCache();

	private boolean forcePrincipalAsString = false;

	protected boolean hideUserNotFoundExceptions = true;

	private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();

	private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userCache, "A user cache must be set");
		Assert.notNull(this.messages, "A message source must be set");
		doAfterPropertiesSet();
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(QrcodeAuthenticationToken.class, authentication,
				() -> this.messages.getMessage("QrcodeAuthenticationProvider.onlySupports",
						"Only QrcodeAuthenticationToken is supported"));
		String username = determineUsername(authentication);
		boolean cacheWasUsed = true;
		UserDetails user = this.userCache.getUserFromCache(username);
		if (user == null) {
			cacheWasUsed = false;
			try {
				user = retrieveUser(username, (QrcodeAuthenticationToken) authentication);
			} catch (UsernameNotFoundException ex) {
				log.debug("Failed to find user '" + username + "'");
				if (!this.hideUserNotFoundExceptions) {
					throw ex;
				}
				throw new BadCredentialsException(
						this.messages.getMessage("QrcodeAuthenticationProvider.badCredentials", "Bad credentials"));
			}
			Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
		}
		try {
			this.preAuthenticationChecks.check(user);
			additionalAuthenticationChecks(user, (QrcodeAuthenticationToken) authentication);
		} catch (AuthenticationException ex) {
			if (!cacheWasUsed) {
				throw ex;
			}
			// There was a problem, so try again after checking
			// we're using latest data (i.e. not from the cache)
			cacheWasUsed = false;
			user = retrieveUser(username, (QrcodeAuthenticationToken) authentication);
			this.preAuthenticationChecks.check(user);
			additionalAuthenticationChecks(user, (QrcodeAuthenticationToken) authentication);
		}
		this.postAuthenticationChecks.check(user);
		if (!cacheWasUsed) {
			this.userCache.putUserInCache(user);
		}
		Object principalToReturn = user;
		if (this.forcePrincipalAsString) {
			principalToReturn = user.getUsername();
		}
		return createSuccessAuthentication(principalToReturn, authentication, user);
	}

	private String determineUsername(Authentication authentication) {
		return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
	}

	protected void doAfterPropertiesSet() throws Exception {
	}

	public UserCache getUserCache() {
		return this.userCache;
	}

	public boolean isForcePrincipalAsString() {
		return this.forcePrincipalAsString;
	}

	public boolean isHideUserNotFoundExceptions() {
		return this.hideUserNotFoundExceptions;
	}

	public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
		this.forcePrincipalAsString = forcePrincipalAsString;
	}

	/**
	 * By default the <code>AbstractUserDetailsAuthenticationProvider</code> throws
	 * a <code>BadCredentialsException</code> if a username is not found or the
	 * password is incorrect. Setting this property to <code>false</code> will cause
	 * <code>UsernameNotFoundException</code>s to be thrown instead for the former.
	 * Note this is considered less secure than throwing
	 * <code>BadCredentialsException</code> for both exceptions.
	 * 
	 * @param hideUserNotFoundExceptions set to <code>false</code> if you wish
	 *        <code>UsernameNotFoundException</code>s to be thrown instead of the
	 *        non-specific <code>BadCredentialsException</code> (defaults to
	 *        <code>true</code>)
	 */
	public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
		this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (QrcodeAuthenticationToken.class.isAssignableFrom(authentication));
	}

	protected UserDetailsChecker getPreAuthenticationChecks() {
		return this.preAuthenticationChecks;
	}

	/**
	 * Sets the policy will be used to verify the status of the loaded
	 * <tt>UserDetails</tt> <em>before</em> validation of the credentials takes
	 * place.
	 * 
	 * @param preAuthenticationChecks strategy to be invoked prior to
	 *        authentication.
	 */
	public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
		this.preAuthenticationChecks = preAuthenticationChecks;
	}

	protected UserDetailsChecker getPostAuthenticationChecks() {
		return this.postAuthenticationChecks;
	}

	public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
		this.postAuthenticationChecks = postAuthenticationChecks;
	}

	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
	}

	private class DefaultPreAuthenticationChecks implements UserDetailsChecker {

		@Override
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				log.debug("Failed to authenticate since user account is locked");
				throw new LockedException(AbstractQrcodeAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
			}
			if (!user.isEnabled()) {
				log.debug("Failed to authenticate since user account is disabled");
				throw new DisabledException(AbstractQrcodeAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
			}
			if (!user.isAccountNonExpired()) {
				log.debug("Failed to authenticate since user account has expired");
				throw new AccountExpiredException(AbstractQrcodeAuthenticationProvider.this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
			}
		}

	}

	private class DefaultPostAuthenticationChecks implements UserDetailsChecker {

		@Override
		public void check(UserDetails user) {
			if (!user.isCredentialsNonExpired()) {
				log.debug("Failed to authenticate since user account credentials have expired");
				throw new CredentialsExpiredException(AbstractQrcodeAuthenticationProvider.this.messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.credentialsExpired",
						"User credentials have expired"));
			}
		}
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
	protected abstract void additionalAuthenticationChecks(UserDetails userDetails,
			QrcodeAuthenticationToken authentication) throws AuthenticationException;

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
	protected abstract UserDetails retrieveUser(String username, QrcodeAuthenticationToken authentication)
			throws AuthenticationException;

	/**
	 * Creates a successful {@link Authentication} object.
	 * <p>
	 * Protected so subclasses can override.
	 * </p>
	 * <p>
	 * Subclasses will usually store the original credentials the user supplied (not
	 * salted or encoded passwords) in the returned <code>Authentication</code>
	 * object.
	 * </p>
	 * 
	 * @param principal that should be the principal in the returned object (defined
	 *        by the {@link #isForcePrincipalAsString()} method)
	 * @param authentication that was presented to the provider for validation
	 * @param user that was loaded by the implementation
	 * @return the successful authentication token
	 */
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		QrcodeAuthenticationToken result = new QrcodeAuthenticationToken(principal, authentication.getCredentials(),
				((QrcodeAuthenticationToken) authentication).getType(),
				((QrcodeAuthenticationToken) authentication).getMobile(),
				this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());
		log.debug("Authenticated user");
		return result;
	}
}