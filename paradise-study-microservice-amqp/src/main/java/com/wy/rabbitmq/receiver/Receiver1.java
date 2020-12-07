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
 * @apiNote queue注解:value队列的名称,autoDelete是否是一个临时队列,即消费者断开之后是否删除队列
 *              若是需要实现数据持久化,则autoDelete需要设置为false
 * @apiNote exchange注解:value交换器的名称,type交换器的类型,autoDelete当所有绑定队列都不使用时,是否删除队列
 *              若是需要实现数据持久化,则autoDelete需要设置为false
 * @author ParadiseWY
 * @date 2019-04-16 13:40:22
 * @git {@link https://github.com/mygodness100}
 */
@RabbitListener(
		bindings = @QueueBinding(value = @Queue(value = "${mq.direct.queue}", autoDelete = "true"),
				exchange = @Exchange(value = "${mq.direct.exchange}", type = ExchangeTypes.DIRECT),
				key = "${mq.direct.routing.key}"))
@Component
public class Receiver1 {

	@RabbitHandler
	public void receiveMsg(String msg) {
		System.out.println(msg);
	}
}