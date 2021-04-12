package com.wy;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 测试死信队列接收消息
 * 
 * @author 飞花梦影
 * @date 2021-01-04 23:44:42
 * @git {@link https://github.com/mygodness100}
 */
@Component
public class TestDeadQueueListener {

	@RabbitListener(queues = "DEAD-CLOSE-QUEUE")
	public void testDead(String msg) {
		System.out.println(msg);
	}
}