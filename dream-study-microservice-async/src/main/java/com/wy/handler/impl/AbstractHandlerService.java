package com.wy.handler.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wy.annotation.AsyncExecute;
import com.wy.config.AsyncProxy;
import com.wy.constant.AsyncConstant;
import com.wy.convert.AsyncRequestConvert;
import com.wy.dto.AsyncRequestDTO;
import com.wy.entity.AsyncRequestEntity;
import com.wy.handler.HandlerService;
import com.wy.handler.context.AsyncContext;
import com.wy.mq.AsyncProducer;
import com.wy.service.AsyncRequestService;

import dream.framework.core.json.JsonHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractHandlerService
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:00:42
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public abstract class AbstractHandlerService implements HandlerService {

	@Autowired
	private AsyncProxy asyncProxy;

	@Autowired
	protected AsyncRequestConvert asyncRequestConvert;

	@Autowired
	protected AsyncProducer asyncProducer;

	@Autowired
	protected AsyncRequestService asyncReqService;

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public boolean handle(AsyncContext context) {
		// 异步执行数据
		AsyncRequestDTO asyncExecDto = this.getAsyncExecDto(context);
		context.setAsyncRequestDto(asyncExecDto);
		// 执行异步策略
		boolean success = this.execute(context);
		if (!success) {
			// 最终兜底方案直接执行
			try {
				context.getJoinPoint().proceed();
			} catch (Throwable e) {
				log.error("兜底方案依然执行失败：{}", asyncExecDto, e);
				log.error("人工处理,queue：{},message：{}", applicationName + AsyncConstant.QUEUE_SUFFIX,
						JsonHelpers.toJson(asyncExecDto));
			}
		}
		return true;
	}

	/**
	 * 保存数据库
	 *
	 * @param asyncExecDto
	 * @param execStatus
	 * @return
	 */
	public AsyncRequestEntity saveAsyncReq(AsyncRequestDTO asyncExecDto, Integer execStatus) {
		AsyncRequestEntity asyncReq = asyncRequestConvert.convert(asyncExecDto);
		try {
			asyncReq.setExecStatus(execStatus);
			asyncReqService.save(asyncReq);
			log.info("异步执行保存数据库成功：{}", asyncReq);
			return asyncReq;
		} catch (Exception e) {
			log.error("异步执行保存数据库失败：{}", asyncReq, e);
			return null;
		}
	}

	/**
	 * AsyncExecDto
	 * 
	 * @param context
	 * @return
	 */
	private AsyncRequestDTO getAsyncExecDto(AsyncContext context) {
		ProceedingJoinPoint joinPoint = context.getJoinPoint();
		AsyncExecute asyncExec = context.getAsyncExecute();
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		AsyncRequestDTO asyncExecDto = new AsyncRequestDTO();
		asyncExecDto.setAppName(applicationName);
		asyncExecDto.setSign(asyncProxy.getAsyncMethodKey(joinPoint.getTarget(), methodSignature.getMethod()));
		asyncExecDto.setClassName(joinPoint.getTarget().getClass().getName());
		asyncExecDto.setMethodName(methodSignature.getMethod().getName());
		asyncExecDto.setAsyncType(asyncExec.type().name());
		asyncExecDto.setParamJson(JsonHelpers.toJson(joinPoint.getArgs()));
		asyncExecDto.setRemark(asyncExec.remark());
		return asyncExecDto;
	}
}