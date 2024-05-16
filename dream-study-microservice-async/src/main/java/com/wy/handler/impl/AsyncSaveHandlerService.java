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
 * 先异步消息处理失败再保存数据库
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:01:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@AllArgsConstructor
public class AsyncSaveHandlerService extends AbstractHandlerService {

	@Override
	public List<String> listType() {
		return Collections.singletonList(AsyncType.ASYNC_SAVE.name());
	}

	@Override
	public boolean execute(AsyncContext context) {
		// 放入消息队列
		boolean success = asyncProducer.send(context.getAsyncRequestDto());
		if (success) {
			return true;
		}
		// 保存数据库
		AsyncRequestEntity asyncReq = this.saveAsyncReq(context.getAsyncRequestDto(), AsyncExecuteStatus.ERROR.getCode());
		return null != asyncReq;
	}
}