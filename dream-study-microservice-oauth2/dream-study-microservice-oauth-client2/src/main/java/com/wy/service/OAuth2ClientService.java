package com.wy.service;

import com.wy.entity.OAuth2ClientEntity;
import com.wy.query.OAuth2ClientQuery;
import com.wy.vo.OAuth2ClientVO;

import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface OAuth2ClientService extends BaseServices<OAuth2ClientEntity, OAuth2ClientVO, OAuth2ClientQuery> {

	/**
	 * 检查是否存在该用户信息,不存在则保存,暂时不做关联基础用户信息,由前端引导完善/关联基础用户信息
	 *
	 * @param oauth2ClientVo 用户信息
	 */
	void checkAndSaveUser(OAuth2ClientVO oauth2ClientVo);
}