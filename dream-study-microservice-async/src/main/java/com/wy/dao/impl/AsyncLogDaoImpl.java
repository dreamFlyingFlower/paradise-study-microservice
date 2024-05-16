package com.wy.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.wy.dao.AsyncLogDao;
import com.wy.entity.AsyncLogEntity;

/**
 * 异步执行日志DAO
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:57:09
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Repository
public class AsyncLogDaoImpl implements AsyncLogDao {

	@Autowired(required = false)
	private JdbcTemplate asyncJdbcTemplate;

	@Override
	public void save(AsyncLogEntity asyncLog) {
		String sql = "insert into async_log(async_id, error_data, create_time) values (?, ?, ?)";
		asyncJdbcTemplate.update(sql, asyncLog.getAsyncId(), asyncLog.getErrorData(), asyncLog.getCreateTime());
	}

	@Override
	public void delete(Long asyncId) {
		String sql = "delete from async_log where async_id = ?";
		asyncJdbcTemplate.update(sql, asyncId);
	}

	@Override
	public String getErrorData(Long asyncId) {
		String sql = "select error_data from async_log where async_id = ? order by id desc limit 1";
		return asyncJdbcTemplate.queryForObject(sql, String.class, asyncId);
	}
}