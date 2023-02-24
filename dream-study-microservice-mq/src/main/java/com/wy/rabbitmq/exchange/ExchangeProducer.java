package com.wy.rabbitmq.exchange;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.extern.slf4j.Slf4j;

/**
 * 交换机ACK生产者
 *
 * @author 飞花梦影
 * @date 2023-02-24 16:16:24
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
public class ExchangeProducer {

	public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ExchangeCallBack exchangeCallBack;

	/**
	 * 依赖注入 rabbitTemplate 之后再设置它的回调对象
	 */
	@PostConstruct
	public void init() {
		rabbitTemplate.setConfirmCallback(exchangeCallBack);
		// 如果交换机发现找不到队列,默认情况会将消息丢弃,可以设置mandatory属性修改该行为,同时要开启配置publisher-returns=true
		// true:交换机无法将消息进行路由时,会将该消息返回给生产者
		// false:如果发现消息无法进行路由,则直接丢弃
		rabbitTemplate.setMandatory(true);
		// 设置回退消息交给谁处理
		rabbitTemplate.setReturnsCallback(exchangeCallBack);
	}

	@GetMapping("sendMessage/{message}")
	public void sendMessage(@PathVariable String message) {
		// 指定消息 id 为 1
		CorrelationData correlationData1 = new CorrelationData("1");
		String routingKey = "key1";
		rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME, routingKey, message + routingKey, correlationData1);
		CorrelationData correlationData2 = new CorrelationData("2");
		routingKey = "key2";
		rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME, routingKey, message + routingKey, correlationData2);
		log.info("发送消息内容:{}", message);
	}
}