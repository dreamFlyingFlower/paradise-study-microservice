package com.wy.enums;

import com.dream.common.StatusMsg;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行状态
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:59:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AsyncExecuteStatus implements StatusMsg {

	/**
	 * 初始化
	 */
	INIT(0, "初始化"),

	/**
	 * 执行失败
	 */
	ERROR(1, "执行失败"),

	/**
	 * 执行成功
	 */
	SUCCESS(2, "执行成功");

	/**
	 * 类型
	 */
	private final Integer code;

	/**
	 * 名称
	 */
	private final String msg;
}