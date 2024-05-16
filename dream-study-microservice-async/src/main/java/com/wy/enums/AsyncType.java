package com.wy.enums;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异步执行类型枚举
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:59:05
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AsyncType {

	/**
	 * 先保存数据库再异步消息处理
	 */
	SAVE_ASYNC("先保存数据库再异步消息处理"),

	/**
	 * 先同步处理失败再保存数据库
	 */
	SYNC_SAVE("先同步处理失败再保存数据库"),

	/**
	 * 先异步消息处理失败再保存数据库
	 */
	ASYNC_SAVE("先异步消息处理失败再保存数据库"),

	/**
	 * 仅异步消息处理
	 */
	ASYNC("仅异步消息处理"),

	/**
	 * 仅异步线程处理
	 */
	THREAD("仅异步线程处理");

	/**
	 * 描述
	 */
	private final String msg;

	public static AsyncType getMsg(String type) {
		return Stream.of(AsyncType.values()).filter(t -> type.equals(t.name())).findFirst().orElse(null);
	}
}