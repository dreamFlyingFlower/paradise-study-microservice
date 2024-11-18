package com.wy.service;

import com.wy.entity.ThirdUserEntity;
import com.wy.query.ThirdUserQuery;
import com.wy.vo.ThirdUserVO;

import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 第三方认证服务用户表
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface ThirdUserService extends BaseServices<ThirdUserEntity, ThirdUserVO, ThirdUserQuery> {

	/**
	 * 检查是否存在该用户信息,不存在则保存,暂时不做关联基础用户信息,由前端引导完善/关联基础用户信息
	 *
	 * @param oauth2ClientVo 用户信息
	 */
	void checkAndSaveUser(ThirdUserVO oauth2ClientVo);
}