package com.wy.social.qq;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import com.wy.service.UserService;

/**
 * Social单独构建的QQ登录,也可以直接写在{@link UserService}中
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 00:36:28
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqService implements SocialUserDetailsService {

	/**
	 * 权限中的ROLE_USER是oauth2登录时需要的权限
	 */
	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		return new SocialUser(userId, "$2a$04$Qq9hGAnd7a7EcHh6UIovzeXLQ9O8KBBaaEPgkSefF89xxDIFlGPge",
				AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_USER"));
	}
}