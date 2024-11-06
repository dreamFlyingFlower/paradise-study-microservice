package com.wy.enums;

import java.util.stream.Stream;

import dream.flying.flower.common.NameMsg;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色成员类型
 * 
 * @author 飞花梦影
 * @date 2024-07-30 21:34:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleMemberType implements NameMsg {

	/** 角色 */
	ROLE("ROLE"),
	/** 用户 */
	USER("USER"),
	/** 菜单 */
	MENU("MENU"),
	/** 动态用户 */
	USER_DYNAMIC("USER-DYNAMIC");

	private String code;

	public static RoleMemberType get(int code) {
		return Stream.of(values()).filter(t -> t.ordinal() == code).findFirst().orElse(null);
	}

	public static RoleMemberType get(String code) {
		return Stream.of(values()).filter(t -> t.name().equalsIgnoreCase(code)).findFirst().orElse(null);
	}
}