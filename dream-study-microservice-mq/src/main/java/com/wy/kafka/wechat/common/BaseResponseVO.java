package com.wy.kafka.wechat.common;

import java.util.UUID;

import lombok.Data;

/**
 * 公共返回对象
 * 
 * @author 飞花梦影
 * @date 2022-07-21 16:49:53
 */
@Data
public class BaseResponseVO<M> {

	private String requestId;

	private M result;

	public static <M> BaseResponseVO<M> success() {
		BaseResponseVO<M> baseResponseVO = new BaseResponseVO<>();
		baseResponseVO.setRequestId(genRequestId());

		return baseResponseVO;
	}

	public static <M> BaseResponseVO<M> success(M result) {
		BaseResponseVO<M> baseResponseVO = new BaseResponseVO<>();
		baseResponseVO.setRequestId(genRequestId());
		baseResponseVO.setResult(result);

		return baseResponseVO;
	}

	private static String genRequestId() {
		return UUID.randomUUID().toString();
	}
}