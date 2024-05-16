package com.wy.service;

import com.wy.entity.AsyncLogEntity;

/**
 * 异步执行日志接口
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:57:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface AsyncLogService {

	/**
	 * 保存
	 *
	 * @param asyncLog
	 */
	void save(AsyncLogEntity asyncLogEntity);

	/**
	 * 删除
	 *
	 * @param asyncId
	 */
	void delete(Long asyncId);

	/**
	 * 获取最后一次失败信息
	 * 
	 * @param asyncId
	 * @return
	 */
	String getErrorData(Long asyncId);
}