package com.wy.service;

import java.util.List;

import com.wy.dto.PageInfoDTO;
import com.wy.entity.AsyncRequestEntity;

/**
 * 异步执行接口
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:57:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface AsyncRequestService {

	/**
	 * 保存
	 * 
	 * @param asyncReq
	 */
	void save(AsyncRequestEntity asyncRequestEntity);

	/**
	 * 更新状态
	 * 
	 * @param id
	 * @param execStatus
	 */
	void updateStatus(Long id, Integer execStatus);

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
	 * @return
	 */
	List<AsyncRequestEntity> listRetry();

	/**
	 * 自动补偿
	 *
	 * @return
	 */
	List<AsyncRequestEntity> listComp();

	/**
	 * 人工执行
	 *
	 * @param pageInfo
	 */
	void listAsyncPage(PageInfoDTO<AsyncRequestEntity> pageInfo);
}