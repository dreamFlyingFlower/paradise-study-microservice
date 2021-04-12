package com.wy.rabbitmq.receiver;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * {@link RabbitListener}:该注解写在类上,而类中需要使用队列的方法上加上@RabbitHandler即可监听队列
 * 
 * bindings:绑定队列,交换器,路由键等<br>
 * {@link QueueBinding}:value->绑定队列,队列名称,exchange->配置交互器,key->路由键<br>
 * {@link Queue}:value->队列名称,autoDelete->即所有消费者断开之后是否删除队列,默认true删除
 * {@link Exchange}:value->交换器名称,type->交换器类型,autoDelete->所有绑定队列都不使用时,是否删除交换器,默认true删除
 * 
 * @author 飞花梦影
 * @date 2019-04-16 13:40:22
 * @git {@link https://github.com/mygodness100}
 */
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${mq.direct.queue}", autoDelete = "true"),
		exchange = @Exchange(value = "${mq.direct.exchange}", type = ExchangeTypes.DIRECT),
		key = "${mq.direct.routing.key}"))
@Component
public class ReceiverDirect {

	@RabbitHandler
	public void receiveMsg(String msg) {
		System.out.println(msg);
	}
}