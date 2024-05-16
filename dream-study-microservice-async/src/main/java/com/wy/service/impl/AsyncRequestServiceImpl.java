package com.wy.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wy.dao.AsyncRequestDao;
import com.wy.dto.PageInfoDTO;
import com.wy.entity.AsyncRequestEntity;
import com.wy.service.AsyncRequestService;

/**
 * 异步执行接口实现
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:58:17
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class AsyncRequestServiceImpl implements AsyncRequestService {

	@Autowired(required = false)
	private AsyncRequestDao asyncReqDao;

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void save(AsyncRequestEntity asyncRequestEntity) {
		asyncRequestEntity.setCreateTime(new Date());
		asyncRequestEntity.setUpdateTime(new Date());
		asyncReqDao.save(asyncRequestEntity);
	}

	@Override
	public void updateStatus(Long id, Integer execStatus) {
		AsyncRequestEntity update = new AsyncRequestEntity();
		update.setId(id);
		update.setExecStatus(execStatus);
		update.setUpdateTime(new Date());
		asyncReqDao.update(update);
	}

	@Override
	public void delete(Long id) {
		asyncReqDao.delete(id);
	}

	@Override
	public AsyncRequestEntity getById(Long id) {
		return asyncReqDao.getById(id);
	}

	@Override
	public List<AsyncRequestEntity> listRetry() {
		return asyncReqDao.listRetry(applicationName);
	}

	@Override
	public List<AsyncRequestEntity> listComp() {
		return asyncReqDao.listComp(applicationName);
	}

	@Override
	public void listAsyncPage(PageInfoDTO<AsyncRequestEntity> pageInfo) {
		Integer total = asyncReqDao.countAsync(applicationName);
		if (null == total || total == 0) {
			return;
		}
		List<AsyncRequestEntity> list = asyncReqDao.listAsync(applicationName,
				(pageInfo.getPageNum() - 1) * pageInfo.getPageSize(), pageInfo.getPageSize());
		pageInfo.setTotal(total);
		pageInfo.setList(list);
	}
}