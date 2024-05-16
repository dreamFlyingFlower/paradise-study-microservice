package com.wy.handler.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.wy.enums.AsyncType;
import com.wy.handler.context.AsyncContext;

import lombok.AllArgsConstructor;

/**
 * 仅异步消息处理
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:00:53
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@AllArgsConstructor
public class AsyncHandlerService extends AbstractHandlerService {

	@Override
	public List<String> listType() {
		return Collections.singletonList(AsyncType.ASYNC.name());
	}

	@Override
	public boolean execute(AsyncContext context) {
		// 放入消息队列
		return asyncProducer.send(context.getAsyncRequestDto());
	}
}