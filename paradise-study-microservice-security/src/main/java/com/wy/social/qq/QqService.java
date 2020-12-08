package com.wy.social.qq;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * @apiNote social用户登录时的用户信息获取操作,和springsecurity的 UserDetailsService 一样
 * @author ParadiseWY
 * @date 2019年9月23日
 */
public class QqService implements SocialUserDetailsService {

	/**
	 * 权限中的ROLE_USER是oauth2登录时需要的权限
	 */
	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		return new SocialUser(userId,
				"$2a$04$Qq9hGAnd7a7EcHh6UIovzeXLQ9O8KBBaaEPgkSefF89xxDIFlGPge",
				AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_USER"));
	}
}