package com.wy.query;

import dream.flying.flower.db.annotation.Query;
import dream.flying.flower.db.enums.QueryType;
import dream.flying.flower.framework.web.query.AbstractQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资源查询")
public class OAuth2ClientQuery extends AbstractQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "用户表标识")
	@Query
	private Long userId;

	@Schema(description = "三方登录唯一标识")
	@Query(type = QueryType.LIKE)
	private String uniqueId;

	@Schema(description = "三方用户的账号")
	@Query(type = QueryType.LIKE)
	private String clientName;

	@Schema(description = "三方登录获取的认证信息(token)")
	@Query
	private String credentials;

	@Schema(description = "认证方法")
	@Query(type = QueryType.LIKE)
	private Long clientAuthenticationMethods;

	@Schema(description = "认证模式")
	@Query(type = QueryType.LIKE)
	private String authorizationGrantTypes;

	@Schema(description = "博客")
	@Query(type = QueryType.LIKE)
	private String blog;

	@Schema(description = "地址")
	@Query(type = QueryType.LIKE)
	private String location;
}