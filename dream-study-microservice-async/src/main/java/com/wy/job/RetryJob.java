package com.wy.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.wy.entity.AsyncRequestEntity;
import com.wy.service.AsyncBizService;
import com.wy.service.AsyncRequestService;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行失败重试定时任务,cron = 0 0/2 * * * ?
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:01:54
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
public class RetryJob {

	@Autowired
	private AsyncRequestService asyncRequestService;

	@Autowired
	private AsyncBizService asyncBizService;

	@XxlJob("AsyncJob")
	public void execute() {
		// 任务开始时间
		long start = System.currentTimeMillis();
		try {
			log.info("异步重试定时任务执行开始......");
			// 执行任务
			List<AsyncRequestEntity> asyncReqList = asyncRequestService.listRetry();
			if (CollectionUtils.isEmpty(asyncReqList)) {
				return;
			}
			for (AsyncRequestEntity asyncReq : asyncReqList) {
				asyncBizService.invoke(asyncReq);
			}
		} catch (Throwable e) {
			log.error("异步重试定时任务执行失败......", e);
		} finally {
			// 任务结束时间
			long end = System.currentTimeMillis();
			log.info("异步重试定时任务执行结束...... 用时：{}毫秒", end - start);
		}
	}
}