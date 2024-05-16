package com.wy.dto;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 代理方法
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:58:32
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
public class ProxyMethodDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 类实例
	 */
	private Object bean;

	/**
	 * 方法
	 */
	private Method method;
}