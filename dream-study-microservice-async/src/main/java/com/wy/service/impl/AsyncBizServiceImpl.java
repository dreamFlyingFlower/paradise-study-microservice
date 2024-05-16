package com.wy.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.wy.config.AsyncProxy;
import com.wy.constant.AsyncConstant;
import com.wy.convert.AsyncRequestConvert;
import com.wy.dto.AsyncRequestDTO;
import com.wy.dto.ProxyMethodDTO;
import com.wy.entity.AsyncLogEntity;
import com.wy.entity.AsyncRequestEntity;
import com.wy.enums.AsyncExecuteStatus;
import com.wy.enums.AsyncType;
import com.wy.properties.AsyncProperties;
import com.wy.service.AsyncBizService;
import com.wy.service.AsyncLogService;
import com.wy.service.AsyncRequestService;

import dream.framework.core.json.JsonHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行实现
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:52:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
public class AsyncBizServiceImpl implements AsyncBizService {

	@Autowired
	private AsyncRequestService asyncReqService;

	@Autowired
	private AsyncLogService asyncLogService;

	@Autowired
	private AsyncRequestConvert asyncConverter;

	@Autowired
	private AsyncProxy asyncProxy;

	@Autowired
	private AsyncProperties asyncProperties;

	/**
	 * 执行方法
	 *
	 * @param asyncReq
	 * @return
	 */
	@Override
	public boolean invoke(AsyncRequestEntity asyncReq) {
		return this.invoke(asyncConverter.convertt(asyncReq));
	}

	/**
	 * 执行方法
	 *
	 * @param asyncExecDto
	 * @return
	 */
	@Override
	public boolean invoke(AsyncRequestDTO asyncExecDto) {
		if (null == asyncExecDto) {
			return true;
		}
		// 标记
		AsyncConstant.PUBLISH_EVENT.set(Boolean.TRUE);
		// 获取执行的类和方法
		ProxyMethodDTO proxyMethodDto = asyncProxy.getProxyMethod(asyncExecDto.getSign());
		if (null == proxyMethodDto) {
			log.warn("异步执行代理类方法不存在：{}", asyncExecDto);
			return true;
		}

		if (null == asyncExecDto.getId()) {
			// 直接执行
			return this.execute(proxyMethodDto, asyncExecDto);
		} else {
			// 补偿执行
			return this.recoupExecute(proxyMethodDto, asyncExecDto);
		}
	}

	/**
	 * 直接执行
	 *
	 * @param proxyMethodDto
	 * @param asyncExecDto
	 * @return
	 */
	private boolean execute(ProxyMethodDTO proxyMethodDto, AsyncRequestDTO asyncExecDto) {
		try {
			// 执行异步方法
			this.invokeMethod(proxyMethodDto, asyncExecDto);
			return true;
		} catch (Exception e) {
			if (AsyncType.ASYNC.name().equals(asyncExecDto.getAsyncType())
					|| AsyncType.THREAD.name().equals(asyncExecDto.getAsyncType())) {
				// 异步消息和异步线程 执行失败 不保存数据库
				log.error("【{}】执行失败：{}", AsyncType.getMsg(asyncExecDto.getAsyncType()), asyncExecDto, e);
			} else {
				// 保存异步执行请求
				this.saveAsyncReq(asyncExecDto);
			}
			return false;
		}
	}

	/**
	 * 补偿执行
	 *
	 * @param proxyMethodDto
	 * @param asyncExecDto
	 * @return
	 */
	private boolean recoupExecute(ProxyMethodDTO proxyMethodDto, AsyncRequestDTO asyncExecDto) {
		AsyncRequestEntity asyncReq = asyncReqService.getById(asyncExecDto.getId());
		if (null == asyncReq) {
			return true;
		}
		try {
			// 执行异步方法
			this.invokeMethod(proxyMethodDto, asyncExecDto);
			// 更新执行结果
			this.updateAsyncReq(asyncReq);
			return true;
		} catch (Exception e) {
			if (asyncReq.getExecCount() + 1 >= asyncProperties.getAsyncExecute().getCount()) {
				log.error("异步执行方法失败超过{}次：{}", asyncProperties.getAsyncExecute().getCount(), asyncExecDto, e);
			}
			// 执行失败更新执行次数且记录失败日志
			this.saveAsyncLog(asyncReq, e);
			return false;
		}
	}

	/**
	 * 反射执行异步方法
	 * 
	 * @param proxyMethodDto
	 * @param asyncExecDto
	 */
	private void invokeMethod(ProxyMethodDTO proxyMethodDto, AsyncRequestDTO asyncExecDto) {
		log.info("异步执行方法开始：{}", asyncExecDto);
		// 获取参数类型
		Object[] paramTypes = this.getParamType(proxyMethodDto.getMethod(), asyncExecDto.getParamJson());
		// 执行方法
		ReflectionUtils.invokeMethod(proxyMethodDto.getMethod(), proxyMethodDto.getBean(), paramTypes);
		log.info("异步执行方法成功：{}", asyncExecDto);
	}

	/**
	 * 获取方法参数
	 *
	 * @param method
	 * @param data
	 * @return
	 */
	private Object[] getParamType(Method method, String data) {
		Type[] types = method.getGenericParameterTypes();
		if (types.length == 0) {
			return null;
		}
		return JsonHelpers.toObjects(data, types);
	}

	/**
	 * 保存异步执行请求
	 *
	 * @param asyncExecDto
	 */
	private void saveAsyncReq(AsyncRequestDTO asyncRequestDto) {
		AsyncRequestEntity asyncReq = asyncConverter.convert(asyncRequestDto);
		asyncReq.setExecStatus(AsyncExecuteStatus.ERROR.getCode());
		asyncReqService.save(asyncReq);
		log.info("处理失败后保存数据库成功：{}", asyncReq);
	}

	/**
	 * 执行失败更新执行次数且记录失败日志
	 *
	 * @param asyncReq
	 * @param e
	 */
	private void saveAsyncLog(AsyncRequestEntity asyncRequestEntity, Exception e) {
		// 更新状态为失败
		asyncReqService.updateStatus(asyncRequestEntity.getId(), AsyncExecuteStatus.ERROR.getCode());
		// 保存执行失败日志
		AsyncLogEntity asyncLog = new AsyncLogEntity();
		asyncLog.setAsyncId(asyncRequestEntity.getId());
		asyncLog.setErrorData(ExceptionUtils.getStackTrace(e));
		asyncLogService.save(asyncLog);
		log.info("处理失败后保存失败日志成功：{}", asyncRequestEntity);
	}

	/**
	 * 更新异步执行请求
	 *
	 * @param asyncReq
	 */
	private void updateAsyncReq(AsyncRequestEntity asyncReq) {
		if (asyncProperties.getAsyncExecute().isDeleted()) {
			// 删除异步执行请求
			asyncReqService.delete(asyncReq.getId());
		} else {
			// 更新状态为成功
			asyncReqService.updateStatus(asyncReq.getId(), AsyncExecuteStatus.SUCCESS.getCode());
		}
		if (asyncReq.getExecStatus() == AsyncExecuteStatus.ERROR.getCode()) {
			// 删除异步执行日志
			asyncLogService.delete(asyncReq.getId());
		}
	}
}