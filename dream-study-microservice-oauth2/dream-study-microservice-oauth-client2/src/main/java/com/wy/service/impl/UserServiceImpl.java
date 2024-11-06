package com.wy.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wy.convert.UserConvert;
import com.wy.entity.OAuth2ClientEntity;
import com.wy.entity.ResourceEntity;
import com.wy.entity.RoleMemberEntity;
import com.wy.entity.UserEntity;
import com.wy.enums.RoleMemberType;
import com.wy.mapper.OAuth2ClientMapper;
import com.wy.mapper.ResourceMapper;
import com.wy.mapper.RoleMemberMapper;
import com.wy.mapper.UserMapper;
import com.wy.query.UserQuery;
import com.wy.service.UserService;
import com.wy.vo.OAuth2ClientVO;
import com.wy.vo.OAuth2UserinfoVO;
import com.wy.vo.UserVO;

import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import dream.flying.flower.framework.security.constant.ConstSecurity;
import dream.flying.flower.framework.security.constant.ConstOAuthClient;
import lombok.AllArgsConstructor;

/**
 * 用户信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends AbstractServiceImpl<UserEntity, UserVO, UserQuery, UserConvert, UserMapper>
		implements UserService {

	private final RoleMemberMapper roleMemberMapper;

	private final ResourceMapper resourceMapper;

	private final OAuth2ClientMapper oauth2ClientMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 在Security中“username”就代表了用户登录时输入的账号,在重写该方法时它可以代表以下内容：账号、手机号、邮箱、姓名等
		// “username”在数据库中不一定非要是一样的列,它可以是手机号、邮箱,也可以都是,最主要的目的就是根据输入的内容获取到对应的用户信息,如下方所示
		// 通过传入的账号信息查询对应的用户信息
		LambdaQueryWrapper<UserEntity> wrapper = Wrappers.lambdaQuery(UserEntity.class)
				.or(o -> o.eq(UserEntity::getEmail, username))
				.or(o -> o.eq(UserEntity::getMobile, username))
				.or(o -> o.eq(UserEntity::getUsername, username));
		UserEntity basicUser = baseMapper.selectOne(wrapper);
		if (basicUser == null) {
			throw new UsernameNotFoundException("账号不存在");
		}

		// 通过用户角色关联表查询对应的角色
		List<RoleMemberEntity> userRoles = roleMemberMapper.selectList(Wrappers.lambdaQuery(RoleMemberEntity.class)
				.eq(RoleMemberEntity::getMemberId, basicUser.getId())
				.eq(RoleMemberEntity::getType, RoleMemberType.USER.getCode()));
		List<Long> rolesId = Optional.ofNullable(userRoles)
				.orElse(Collections.emptyList())
				.stream()
				.map(RoleMemberEntity::getRoleId)
				.collect(Collectors.toList());
		if (ObjectUtils.isEmpty(rolesId)) {
			return basicUser;
		}

		// 通过角色菜单关联表查出对应的菜单
		List<RoleMemberEntity> roleMenus = roleMemberMapper.selectList(Wrappers.lambdaQuery(RoleMemberEntity.class)
				.in(RoleMemberEntity::getRoleId, rolesId)
				.eq(RoleMemberEntity::getType, RoleMemberType.MENU.getCode()));
		List<Long> menusId = Optional.ofNullable(roleMenus)
				.orElse(Collections.emptyList())
				.stream()
				.map(RoleMemberEntity::getMemberId)
				.collect(Collectors.toList());
		if (ObjectUtils.isEmpty(menusId)) {
			return basicUser;
		}

		// 根据菜单ID查出菜单
		List<ResourceEntity> menus = resourceMapper.selectBatchIds(menusId);
		Set<SimpleGrantedAuthority> authorities = Optional.ofNullable(menus)
				.orElse(Collections.emptyList())
				.stream()
				.map(ResourceEntity::getResourceUrl)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		basicUser.setAuthorities(authorities);
		return basicUser;
	}

	@Override
	public Long saveByThirdAccount(OAuth2ClientVO oauth2ClientVo) {
		UserEntity basicUser = new UserEntity();
		basicUser.setUsername(oauth2ClientVo.getClientName());
		basicUser.setAvatarUrl(oauth2ClientVo.getAvatarUrl());
		basicUser.setSourceFrom(oauth2ClientVo.getAuthorizationGrantTypes());
		this.save(basicUser);
		return basicUser.getId();
	}

	@Override
	public OAuth2UserinfoVO getLoginUserInfo() {
		OAuth2UserinfoVO result = new OAuth2UserinfoVO();

		// 获取当前认证信息
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 其它非token方式获取用户信息
		if (!(authentication instanceof JwtAuthenticationToken)) {
			BeanUtils.copyProperties(authentication.getPrincipal(), result);
			result.setSub(authentication.getName());
			return result;
		}

		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
		// 获取jwt解析内容
		Jwt token = jwtAuthenticationToken.getToken();

		// 获取当前登录类型
		String loginType = token.getClaim(ConstOAuthClient.OAUTH2_LOGIN_TYPE);
		// 获取用户唯一Id
		String uniqueId = token.getClaimAsString(ConstOAuthClient.TOKEN_UNIQUE_ID);
		// 基础用户信息id
		Long basicUserId = null;

		// 获取Token中的权限列表
		List<String> claimAsStringList = token.getClaimAsStringList(ConstSecurity.AUTHORITIES_KEY);

		// 如果登录类型不为空则代表是三方登录,获取三方用户信息
		if (!org.springframework.util.ObjectUtils.isEmpty(loginType)) {
			// 根据三方登录类型与三方用户的唯一Id查询用户信息
			LambdaQueryWrapper<OAuth2ClientEntity> wrapper = Wrappers.lambdaQuery(OAuth2ClientEntity.class)
					.eq(OAuth2ClientEntity::getUniqueId, uniqueId)
					.eq(OAuth2ClientEntity::getAuthorizationGrantTypes, loginType);
			OAuth2ClientEntity oauth2ThirdAccount = oauth2ClientMapper.selectOne(wrapper);
			if (oauth2ThirdAccount != null) {
				basicUserId = oauth2ThirdAccount.getUserId();
				// 复制三方用户信息
				BeanUtils.copyProperties(oauth2ThirdAccount, result);
			}
		} else {
			// 为空则代表是使用当前框架提供的登录接口登录的,转为基础用户信息
			basicUserId = Long.parseLong(uniqueId);
		}

		if (basicUserId == null) {
			// 如果用户id为空,代表获取三方用户信息失败
			result.setSub(authentication.getName());
			return result;
		}

		// 查询基础用户信息
		UserEntity basicUser = this.getById(basicUserId);
		if (basicUser != null) {
			BeanUtils.copyProperties(basicUser, result);
		}

		// 填充权限信息
		if (!org.springframework.util.ObjectUtils.isEmpty(claimAsStringList)) {
			Set<SimpleGrantedAuthority> authorities =
					claimAsStringList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
			// 否则设置为token中获取的
			result.setAuthorities(authorities);
		}

		result.setSub(authentication.getName());
		return result;
	}
}