package com.wy.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.dao.AsyncLogDao;
import com.wy.entity.AsyncLogEntity;
import com.wy.service.AsyncLogService;

/**
 * 异步执行日志接口实现
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:58:07
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Service
public class AsyncLogServiceImpl implements AsyncLogService {

	@Autowired(required = false)
	private AsyncLogDao asyncLogDao;

	@Override
	public void save(AsyncLogEntity asyncLog) {
		asyncLog.setCreateTime(new Date());
		asyncLogDao.save(asyncLog);
	}

	@Override
	public void delete(Long asyncId) {
		asyncLogDao.delete(asyncId);
	}

	@Override
	public String getErrorData(Long asyncId) {
		return asyncLogDao.getErrorData(asyncId);
	}
}