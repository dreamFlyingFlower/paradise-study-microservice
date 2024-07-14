package com.wy.exception;

import org.springframework.security.core.AuthenticationException;

import dream.flying.flower.enums.TipEnum;

/**
 * 自定义安全认证请求异常
 * 
 * 在securiy的所有登录,认证请求中,必须使用继承{@link AuthenticationException}的类,否则自定义异常将无法正确返回给前端
 * 
 * @author 飞花梦影
 * @date 2019-09-30 16:34:24
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class AuthException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	private int code;

	public int getCode() {
		return code;
	}

	public AuthException() {
		this(TipEnum.TIP_SYS_ERROR);
	}

	public AuthException(TipEnum tipEnum) {
		this(tipEnum.getCode(), tipEnum.getMsg(), null);
	}

	public AuthException(CharSequence message) {
		this(0, message);
	}

	public AuthException(int code, CharSequence message) {
		this(code, message, null);
	}

	public AuthException(int code, Throwable ex) {
		this(code, ex.getMessage(), ex);
	}

	public AuthException(CharSequence message, Throwable ex) {
		this(0, message, ex);
	}

	public AuthException(int code, CharSequence message, Throwable ex) {
		super(message.toString(), ex);
		this.code = code;
	}
}