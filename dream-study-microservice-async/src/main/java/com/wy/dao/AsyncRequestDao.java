package com.wy.dao;

import java.util.List;

import com.wy.entity.AsyncRequestEntity;

/**
 * 异步执行 dao
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:56:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface AsyncRequestDao {

	/**
	 * 保存
	 *
	 * @param asyncRequestEntity
	 */
	void save(AsyncRequestEntity asyncRequestEntity);

	/**
	 * 更新
	 *
	 * @param asyncRequestEntity
	 */
	void update(AsyncRequestEntity asyncRequestEntity);

	/**
	 * 删除
	 *
	 * @param id
	 */
	void delete(Long id);

	/**
	 * 根据ID查询
	 *
	 * @param id
	 * @return
	 */
	AsyncRequestEntity getById(Long id);

	/**
	 * 自动重试
	 * 
	 * @param appName
	 * @return
	 */
	List<AsyncRequestEntity> listRetry(String appName);

	/**
	 * 自动补偿
	 * 
	 * @param appName
	 * @return
	 */
	List<AsyncRequestEntity> listComp(String appName);

	/**
	 * 人工执行总数量
	 *
	 * @param appName
	 * @return
	 */
	Integer countAsync(String appName);

	/**
	 * 人工执行
	 *
	 * @param appName
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<AsyncRequestEntity> listAsync(String appName, int pageIndex, int pageSize);
}