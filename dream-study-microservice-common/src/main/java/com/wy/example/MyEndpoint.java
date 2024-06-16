package com.wy.example;

import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import dream.flying.flower.collection.MapHelper;

/**
 * 自定义端点,类似于info,health等监控地址
 *
 * @author 飞花梦影
 * @date 2023-04-03 14:30:45
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@Endpoint(id = "myEndPoint")
public class MyEndpoint {

	/**
	 * 展示在页面上的信息,不能有参数
	 * 
	 * @return Map<String, Object>
	 */
	@ReadOperation
	public Map<String, Object> read() {
		return MapHelper.builder().put("info", "test").build();
	}

	/**
	 * 调用端点时使用的方法
	 */
	@WriteOperation
	public void write() {
		System.out.println("调用了端点方法....");
	}
}