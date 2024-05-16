package com.wy.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dream.lang.StrHelper;
import com.dream.result.Result;
import com.wy.dto.PageInfoDTO;
import com.wy.entity.AsyncRequestEntity;
import com.wy.service.AsyncBizService;
import com.wy.service.AsyncLogService;
import com.wy.service.AsyncRequestService;

import dream.framework.web.controller.BaseController;
import lombok.AllArgsConstructor;

/**
 * 异步执行API
 *
 * @author 飞花梦影
 * @date 2024-05-16 13:56:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@RequestMapping("/dream/async")
@AllArgsConstructor
public class AsyncController implements BaseController {

	private final AsyncRequestService asyncRequestService;

	private final AsyncLogService asyncLogService;

	private final AsyncBizService asyncBizService;

	@PostMapping("/page")
	public Result<?> page(@RequestBody PageInfoDTO<AsyncRequestEntity> pageInfo) {
		asyncRequestService.listAsyncPage(pageInfo);
		return ok(pageInfo);
	}

	@PostMapping("/detail/{id}")
	public Result<?> detail(@PathVariable("id") Long id) {
		AsyncRequestEntity asyncRequestEntity = asyncRequestService.getById(id);
		if (null == asyncRequestEntity) {
			return error("异步任务不存在");
		}
		Map<String, Object> dataMap = new HashMap<>();
		if (StrHelper.isNotEmpty(asyncRequestEntity.getParamJson())) {
			asyncRequestEntity.setParamJson(asyncRequestEntity.getParamJson().replace("\"", ""));
		}
		dataMap.put("req", asyncRequestEntity);
		dataMap.put("log", asyncLogService.getErrorData(id));
		return ok(dataMap);
	}

	@PostMapping("/exec/{id}")
	public Result<?> exec(@PathVariable("id") Long id) {
		AsyncRequestEntity asyncReq = asyncRequestService.getById(id);
		if (null == asyncReq) {
			return error("异步任务不存在");
		}
		if (asyncBizService.invoke(asyncReq)) {
			return ok("执行成功");
		} else {
			return error("执行失败");
		}
	}

	@PostMapping("/delete/{id}")
	public Result<?> delete(@PathVariable("id") Long id) {
		asyncRequestService.delete(id);
		asyncLogService.delete(id);
		return ok("删除成功");
	}
}