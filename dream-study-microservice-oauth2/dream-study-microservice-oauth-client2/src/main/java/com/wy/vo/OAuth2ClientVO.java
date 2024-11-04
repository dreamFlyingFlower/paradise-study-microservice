package com.wy.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.fhs.core.trans.vo.TransPojo;

import dream.flying.flower.framework.web.valid.ValidAdd;
import dream.flying.flower.framework.web.valid.ValidEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册到其他认证服务器的信息
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注册到其他认证服务器的信息")
public class OAuth2ClientVO implements Serializable, TransPojo, OAuth2User {

	private static final long serialVersionUID = 1L;

	@Schema(description = "主键")
	@NotNull(message = "id不能为空", groups = { ValidEdit.class })
	private Long id;

	@Schema(description = "用户表标识")
	@NotNull(message = "用户表标识不能为空", groups = { ValidAdd.class })
	private Long userId;

	@Schema(description = "三方登录唯一标识")
	@NotBlank(message = "三方登录唯一标识不能为空", groups = { ValidEdit.class })
	@Size(max = 64, message = "三方登录唯一标识最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String uniqueId;

	@Schema(description = "三方用户的账号")
	@NotBlank(message = "三方用户的账号不能为空", groups = { ValidEdit.class })
	@Size(max = 64, message = "三方用户的账号最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String clientName;

	@Schema(description = "三方登录获取的认证信息(token)")
	@Size(max = 256, message = "三方登录获取的认证信息(token)最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String credentials;

	@Schema(description = "三方登录获取的认证信息过期时间")
	private Date credentialsExpiresAt;

	@Schema(description = "认证方法")
	@Size(max = 256, message = "认证方法最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String clientAuthenticationMethods;

	@Schema(description = "认证模式")
	@NotBlank(message = "认证模式不能为空", groups = { ValidEdit.class })
	@Size(max = 256, message = "认证模式最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String authorizationGrantTypes;

	@Schema(description = "博客")
	@Size(max = 256, message = "博客最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String blog;

	@Schema(description = "地址")
	@Size(max = 256, message = "地址最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String location;

	@Schema(description = "名称")
	@Size(max = 64, message = "名称最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String name;

	@Schema(description = "头像")
	@Size(max = 256, message = "地址最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String avatarUrl;

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}
}