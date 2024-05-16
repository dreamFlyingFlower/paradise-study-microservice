package com.wy.handler.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.wy.entity.AsyncRequestEntity;
import com.wy.enums.AsyncExecuteStatus;
import com.wy.enums.AsyncType;
import com.wy.handler.context.AsyncContext;

import lombok.AllArgsConstructor;

/**
 * 先保存数据库再异步消息处理
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:01:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@AllArgsConstructor
public class SaveAsyncHandlerService extends AbstractHandlerService {

	@Override
	public List<String> listType() {
		return Collections.singletonList(AsyncType.SAVE_ASYNC.name());
	}

	@Override
	public boolean execute(AsyncContext context) {
		// 保存数据库
		AsyncRequestEntity asyncRequestEntity =
				this.saveAsyncReq(context.getAsyncRequestDto(), AsyncExecuteStatus.INIT.getCode());
		if (null == asyncRequestEntity) {
			// 降级为仅异步消息处理
			return asyncProducer.send(context.getAsyncRequestDto());
		}
		// 放入消息队列（需要数据库ID）
		boolean success = asyncProducer.send(asyncRequestConvert.convertt(asyncRequestEntity));
		if (success) {
			return true;
		}
		// 更新状态为失败
		asyncReqService.updateStatus(asyncRequestEntity.getId(), AsyncExecuteStatus.ERROR.getCode());
		return true;
	}
}