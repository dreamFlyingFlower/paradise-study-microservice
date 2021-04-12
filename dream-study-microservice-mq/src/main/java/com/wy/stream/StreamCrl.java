package com.wy.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @apiNote springstream发送消息,注意messagebuilder的包
 * @author ParadiseWY
 * @date 2019年9月15日 上午7:42:12
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