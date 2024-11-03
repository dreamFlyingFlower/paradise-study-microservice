package com.wy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wy.entity.OAuth2ClientEntity;
import com.wy.query.OAuth2ClientQuery;
import com.wy.vo.OAuth2ClientVO;

import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface OAuth2ClientMapper extends BaseMappers<OAuth2ClientEntity, OAuth2ClientVO, OAuth2ClientQuery> {

}