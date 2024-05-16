package com.wy.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.dream.collection.CollectionHelper;
import com.wy.dao.AsyncRequestDao;
import com.wy.entity.AsyncRequestEntity;
import com.wy.properties.AsyncProperties;

/**
 * 异步执行 dao
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:56:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Repository
public class AsyncRequestDaoImpl implements AsyncRequestDao {

	@Autowired(required = false)
	private JdbcTemplate asyncJdbcTemplate;

	@Autowired
	private AsyncProperties asyncProperties;

	@Override
	public void save(AsyncRequestEntity asyncRequestEntity) {
		String sql =
				"insert into async_request(app_name, sign, class_name, method_name, async_type, param_json, remark, exec_status) values (?, ?, ?, ?, ?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		asyncJdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, asyncRequestEntity.getAppName());
			ps.setString(2, asyncRequestEntity.getSign());
			ps.setString(3, asyncRequestEntity.getClassName());
			ps.setString(4, asyncRequestEntity.getMethodName());
			ps.setString(5, asyncRequestEntity.getAsyncType());
			ps.setString(6, asyncRequestEntity.getParamJson());
			ps.setString(7, asyncRequestEntity.getRemark());
			ps.setInt(8, asyncRequestEntity.getExecStatus());
			return ps;
		}, keyHolder);
		asyncRequestEntity.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
	}

	@Override
	public void update(AsyncRequestEntity asyncRequestEntity) {
		String sql =
				"update async_request set exec_status = ?, exec_count  = exec_count + 1, update_time = ? where id = ?";
		asyncJdbcTemplate.update(sql, asyncRequestEntity.getExecStatus(), asyncRequestEntity.getUpdateTime(),
				asyncRequestEntity.getId());
	}

	@Override
	public void delete(Long id) {
		String sql = "delete from async_request where id = ?";
		asyncJdbcTemplate.update(sql, id);
	}

	@Override
	public AsyncRequestEntity getById(Long id) {
		String sql = "select * from async_request where id = ?";
		List<AsyncRequestEntity> list =
				asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncRequestEntity.class), id);
		return CollectionHelper.isEmpty(list) ? null : list.get(0);
	}

	@Override
	public List<AsyncRequestEntity> listRetry(String applicationName) {
		String sql =
				"select * from async_request where exec_status = 1 and exec_count < ? and app_name = ? order by id limit ?";
		return asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncRequestEntity.class),
				asyncProperties.getAsyncExecute().getCount(), applicationName,
				asyncProperties.getAsyncExecute().getRetryLimit());
	}

	@Override
	public List<AsyncRequestEntity> listComp(String appName) {
		String sql =
				"select * from async_request where exec_status = 0 and exec_count = 0 and date_add(create_time, interval 1 hour) < now() and app_name = ? order by id limit ?";
		return asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncRequestEntity.class), appName,
				asyncProperties.getAsyncExecute().getCompensateLimit());
	}

	@Override
	public Integer countAsync(String appName) {
		String sql = "select count(*) from async_request where exec_status = 1 and exec_count >= ? and app_name = ?";
		return asyncJdbcTemplate.queryForObject(sql, Integer.class, asyncProperties.getAsyncExecute().getCount(),
				appName);
	}

	@Override
	public List<AsyncRequestEntity> listAsync(String appName, int pageIndex, int pageSize) {
		String sql =
				"select * from async_request where exec_status = 1 and exec_count >= ? and app_name = ? order by id limit ?, ?";
		return asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncRequestEntity.class),
				asyncProperties.getAsyncExecute().getCount(), appName, pageIndex, pageSize);
	}
}