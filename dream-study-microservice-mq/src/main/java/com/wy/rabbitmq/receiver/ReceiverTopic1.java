package com.wy.rabbitmq.receiver;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @apiNote 将listener写在类上,而放上中需要使用队列的方法上加上RabbitHandler注解
 * @apiNote bindings注解:value:绑定队列,队列名称,exchange:配置交互器,key:路由键
 * @apiNote queue注解:value队列的名称,autoDelete是否是一个可删除的临时队列
 * @apiNote exchange注解:value交换器的名称,type交换器的类型
 * @author ParadiseWY
 * @date 2019-04-16 13:40:22
 * @git {@link https://github.com/mygodness100}
 */
@RabbitListener(bindings = @QueueBinding(
		value = @Queue(value = "${mq.top.queue.info}", autoDelete = "true"),
		exchange = @Exchange(value = "${mq.top.exchange}", type = ExchangeTypes.TOPIC),
		key = "*.info.log"))
@Component
public class ReceiverTopic1 {

	@RabbitHandler
	public void receiveMsg(String msg) {
		System.out.println(msg);
	}
}