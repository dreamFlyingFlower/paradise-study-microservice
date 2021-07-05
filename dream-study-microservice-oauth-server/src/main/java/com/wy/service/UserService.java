//package com.wy.service;
//
//import java.util.Collection;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
///**
// * 自定义用户实现
// *
// * @author 飞花梦影
// * @date 2021-07-05 12:14:03
// * @git {@link https://github.com/dreamFlyingFlower }
// */
//@Service
//public class UserService implements UserDetailsService {
//
//	/**
//	 * 实现UserDetailsService中的loadUserByUsername方法，用于加载用户数据
//	 */
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//		return new UserDetails() {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public boolean isEnabled() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean isCredentialsNonExpired() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean isAccountNonLocked() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean isAccountNonExpired() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public String getUsername() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public String getPassword() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public Collection<? extends GrantedAuthority> getAuthorities() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
//	}
//}