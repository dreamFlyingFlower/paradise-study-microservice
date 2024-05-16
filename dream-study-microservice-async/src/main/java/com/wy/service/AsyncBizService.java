package com.wy.service;

import com.wy.dto.AsyncRequestDTO;
import com.wy.entity.AsyncRequestEntity;

/**
 * 异步执行接口
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:51:50
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface AsyncBizService {

	/**
	 * 执行方法
	 *
	 * @param asyncReq
	 * @return 是否成功
	 */
	boolean invoke(AsyncRequestEntity asyncRequestEntity);

	/**
	 * 执行方法
	 *
	 * @param asyncExecDto
	 * @return 是否成功
	 */
	boolean invoke(AsyncRequestDTO asyncRequestDto);
}