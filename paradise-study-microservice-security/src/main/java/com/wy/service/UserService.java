package com.wy.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.wy.entity.User;

/**
 * 配合security使用,userservice必须实现userdetailservice接口
 * @author paradiseWy
 */
@Service
public class UserService implements UserDetailsService,SocialUserDetailsService {

	/**
	 * 参数为登录时的username,可通过该username查找用户的详细信息,前提是username唯一
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 需要根据用户名从数据库获取,此处演示直接写死
		if(Objects.equal("admin", username)) {
			User user = new User();
			user.setUsername(username);
			user.setPassword("$2a$04$Qq9hGAnd7a7EcHh6UIovzeXLQ9O8KBBaaEPgkSefF89xxDIFlGPge");
			// 账户是否过期
			user.setAccountNonExpired(true);
			// 账户是否锁定,可以恢复到正常状态
			user.setAccountNonLocked(true);
			// 密码是否过期
			user.setCredentialsNonExpired(true);
			// 账户是否可用,逻辑删除了的账户不可用
			user.setEnabled(true);
			return user;
		}
		return new User();
	}

	/**
	 * 第三方登录时需要构建的user
	 */
	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		// 需要根据用户名从数据库获取,此处演示直接写死
		return new SocialUser(userId, "123456", AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN"));
	}
}