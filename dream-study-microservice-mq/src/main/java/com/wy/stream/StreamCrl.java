package com.wy.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * springstream发送消息,注意messagebuilder的包
 * 
 * @author 飞花梦影
 * @date 2019-09-15 07:42:12
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@RestController
public class StreamCrl {

	@Autowired
	private StreamClient streamClient;

	@GetMapping("sendMsg")
	public void process() {
		streamClient.outPutChannel().send(MessageBuilder.withPayload("fdsfds").build());
	}
}