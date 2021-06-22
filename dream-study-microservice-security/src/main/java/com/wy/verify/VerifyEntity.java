package com.wy.verify;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 校验参数实体类,通用参数
 * 
 * @auther 飞花梦影
 * @date 2019-09-24 22:58:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
public class VerifyEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 校验码
	 */
	@ApiModelProperty("校验码")
	private String verityCode;

	/**
	 * 过期时间
	 */
	@ApiModelProperty("过期时间")
	private LocalDateTime expireTime;

	/**
	 * 构造函数
	 * 
	 * @param verityCode 验证码
	 * @param expireSeconds 过期时间,单位秒
	 */
	public VerifyEntity(String verityCode, int expireSeconds) {
		this.verityCode = verityCode;
		this.expireTime = LocalDateTime.now().plusSeconds(expireSeconds);
	}

	public VerifyEntity(String verityCode, LocalDateTime expireTime) {
		this.verityCode = verityCode;
		this.expireTime = expireTime;
	}

	public boolean isExpried() {
		return LocalDateTime.now().isAfter(expireTime);
	}
}