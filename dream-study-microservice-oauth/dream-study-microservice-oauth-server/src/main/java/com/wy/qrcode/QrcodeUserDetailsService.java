package com.wy.qrcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.wy.convert.UserConvert;
import com.wy.entity.ResourceEntity;
import com.wy.entity.RoleEntity;
import com.wy.entity.UserEntity;
import com.wy.mapper.ResourceMapper;
import com.wy.mapper.RoleMapper;
import com.wy.mapper.UserMapper;
import com.wy.query.UserQuery;
import com.wy.service.UserService;
import com.wy.vo.UserVO;

import dream.flying.flower.collection.CollectionHelper;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务
 *
 * @author 飞花梦影
 * @date 2024-10-30 11:11:16
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class QrcodeUserDetailsService
		extends AbstractServiceImpl<UserEntity, UserVO, UserQuery, UserConvert, UserMapper> implements UserService {

	RoleMapper roleMapper;

	ResourceMapper resourceMapper;

	RedisTemplate<Object, Object> redisTemplate;

	@Override
	public UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException {
		UserEntity userEntity;
		String[] parameter = var1.split(":");
		// 手机验证码调用FeignClient根据电话号码查询用户
		if ("phone".equals(parameter[0])) {
			UserVO userVo = baseMapper.getUserByPhone(parameter[1]);
			if (null == userVo) {
				log.error("找不到该用户,手机号码:" + parameter[1]);
				throw new UsernameNotFoundException("找不到该用户,手机号码:" + parameter[1]);
			}
			userEntity = baseConvert.convert(userVo);
		} else if ("qr".equals(parameter[0])) {
			// 扫码登陆根据key从redis查询用户
			userEntity = null;
		} else {
			UserVO userVo = baseMapper.getUserByUserName(parameter[1]);
			if (null == userVo) {
				log.error("找不到该用户,用户名:" + parameter[1]);
				throw new UsernameNotFoundException("找不到该用户,用户名:" + parameter[1]);
			}
			userEntity = baseConvert.convert(userVo);
		}

		// 查询角色
		List<RoleEntity> roleEntities = roleMapper.queryRolesByUserId(userEntity.getId());
		if (CollectionHelper.isEmpty(roleEntities)) {
			log.error("查询角色失败！");
			roleEntities = new ArrayList<>();
		}

		// 获取用户权限列表
		Set<SimpleGrantedAuthority> authorities = Optional.ofNullable(roleEntities)
				.orElse(Collections.emptyList())
				.stream()
				.map(RoleEntity::getRoleCode)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		userEntity.setAuthorities(authorities);

		// 查询菜单
		List<ResourceEntity> resourceEntities = resourceMapper.getMenusByUserId(userEntity.getId());

		// 存储菜单到redis
		if (CollectionHelper.isEmpty(resourceEntities)) {
			redisTemplate.delete(userEntity.getId() + "-menu");
			resourceEntities.forEach(e -> {
				redisTemplate.opsForList().leftPush(userEntity.getId() + "-menu", e);
			});
		}
		return userEntity;
	}
}