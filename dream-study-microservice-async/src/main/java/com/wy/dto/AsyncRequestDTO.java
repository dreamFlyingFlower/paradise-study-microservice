package com.wy.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 异步执行DTO
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:58:51
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
public class AsyncRequestDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 应用名称
	 */
	private String appName;

	/**
	 * 方法签名
	 */
	private String sign;

	/**
	 * 全路径类名称
	 */
	private String className;

	/**
	 * method名称
	 */
	private String methodName;

	/**
	 * 异步策略类型
	 */
	private String asyncType;

	/**
	 * 参数json字符串
	 */
	private String paramJson;

	/**
	 * 业务描述
	 */
	private String remark;
}