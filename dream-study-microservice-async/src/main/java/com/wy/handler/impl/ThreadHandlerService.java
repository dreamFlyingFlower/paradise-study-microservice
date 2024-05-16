package com.wy.handler.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wy.enums.AsyncType;
import com.wy.handler.context.AsyncContext;
import com.wy.service.AsyncBizService;

/**
 * 异步线程处理
 *
 * @author 飞花梦影
 * @date 2024-05-16 14:01:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class ThreadHandlerService extends AbstractHandlerService {

	@Autowired(required = false)
	private Executor asyncExecute;

	@Autowired
	private AsyncBizService asyncBizService;

	@Override
	public List<String> listType() {
		return Collections.singletonList(AsyncType.THREAD.name());
	}

	@Override
	public boolean execute(AsyncContext context) {
		asyncExecute.execute(() -> asyncBizService.invoke(context.getAsyncRequestDto()));
		return true;
	}
}