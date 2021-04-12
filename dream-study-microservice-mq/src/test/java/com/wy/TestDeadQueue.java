package com.wy;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试死信队列
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:43:11
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootTest
public class TestDeadQueue {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void testTTL() throws IOException {
		rabbitTemplate.convertAndSend("DEAD-EXCHANGE", "order.create", "hello world!");
		System.in.read();
	}
}