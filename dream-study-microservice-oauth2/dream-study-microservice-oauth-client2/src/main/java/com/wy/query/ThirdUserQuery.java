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
 * 第三方认证服务用户表
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
@Schema(description = "第三方认证服务用户查询")
public class ThirdUserQuery extends AbstractQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "用户表标识")
	@Query
	private Long userId;

	@Schema(description = "三方登录唯一标识")
	@Query(type = QueryType.LIKE)
	private String uniqueId;

	@Schema(description = "三方用户的账号")
	@Query(type = QueryType.LIKE)
	private String thirdUsername;

	@Schema(description = "三方登录获取的认证信息(token)")
	@Query
	private String credentials;

	@Schema(description = "三方登录类型")
	@Query(type = QueryType.LIKE)
	private String type;

	@Schema(description = "博客")
	@Query(type = QueryType.LIKE)
	private String blog;

	@Schema(description = "地址")
	@Query(type = QueryType.LIKE)
	private String location;
}