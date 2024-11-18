package com.wy.service.impl;

import org.springframework.util.ObjectUtils;

import com.wy.convert.ThirdUserConvert;
import com.wy.entity.ThirdUserEntity;
import com.wy.mapper.ThirdUserMapper;
import com.wy.query.ThirdUserQuery;
import com.wy.service.ThirdUserService;
import com.wy.service.UserService;
import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-11-03 10:48:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class ThirdUserServiceImpl
		extends AbstractServiceImpl<ThirdUserEntity, ThirdUserVO, ThirdUserQuery, ThirdUserConvert, ThirdUserMapper>
		implements ThirdUserService {

	private final UserService userService;

	@Override
	public void checkAndSaveUser(ThirdUserVO oauth2ClientVo) {
		// 构建三方唯一id和三方登录方式的查询条件
		ThirdUserEntity oauth2ClientEntity = this.lambdaQuery()
				.eq(ThirdUserEntity::getType, oauth2ClientVo.getType())
				.eq(ThirdUserEntity::getUniqueId, oauth2ClientVo.getUniqueId())
				.one();
		if (oauth2ClientEntity == null) {
			// 生成用户信息
			Long userId = userService.saveByThirdAccount(oauth2ClientVo);
			oauth2ClientVo.setUserId(userId);
			// 不存在保存用户信息
			this.save(baseConvert.convert(oauth2ClientVo));
		} else {
			// 校验是否需要生成基础用户信息
			if (ObjectUtils.isEmpty(oauth2ClientEntity.getUserId())) {
				// 生成用户信息
				Long userId = userService.saveByThirdAccount(oauth2ClientVo);
				oauth2ClientEntity.setUserId(userId);
			}
			// 存在更新用户的认证信息
			oauth2ClientEntity.setCredentials(oauth2ClientVo.getCredentials());
			oauth2ClientEntity.setCredentialsExpiresAt(oauth2ClientVo.getCredentialsExpiresAt());
			this.updateById(oauth2ClientEntity);
		}
	}
}