package com.wy.dao;

import com.wy.entity.AsyncLogEntity;

/**
 * 异步执行日志DAO
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:56:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface AsyncLogDao {

	/**
	 * 保存
	 *
	 * @param asyncLogEntity
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