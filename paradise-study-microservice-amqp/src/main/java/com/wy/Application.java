package com.wy;

import javax.jms.Message;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.jms.annotation.EnableJms;

/**
 * Amqp:消息中间件,主要实现有RabbitMQ,ActiveMQ,RocketMQ,Kafka等,Spring默认的实现是RabbitMQ.<br>
 * 消息中间件的主要功能是提升系统异步通信,扩展解耦,高并发时的请求限制,数据库流量限制等<br>
 * 消息中的两个重要概念:消息代理(message broker)和目的地(destination),当消息发送者发送消息后,将由消息代理接管,
 * 消息代理保证消息传递到指定目的地,同时还需要保证消息的一致性,不能重复消息,消息确认等
 * 
 * Amqp是JMS的实现,Spring中主要配置类为{@link JmsAutoConfiguration},{@link RabbitAutoConfiguration},<br>
 * 若需要使用JMS或Amqp,需要开启{@link EnableJms}或{@link EnableRabbit}
 * 
 * JMS支持的消息类型:TextMessage,MapMessage,BytesMessage,StreamMessage,ObjectMessage,<br>
 * 实际上都是{@link Message}的子接口,而Amqp的消息传递时实际上都是序列化成byte[]传输,可以转换为复杂的其他类型
 * 
 * 消息队列的多种形式,不同实现有不同形式,但以下两种是都有的:<br>
 * 队列(queue):点对点消息通信,消息被放在队列中,可以有多个队列同时获取消息,但是消息只能被消费一次,消费完就移除
 * 主题(topic):发布(publish),订阅(subscribu)消息通信,消息被发送到主题,多个订阅者监听该主题,消息被消费多次
 * 
 * RabbitMQ提供了5种消息模式:除了direct,其他模型都和主题类似,只是在路由上有所不同<br>
 * direct:点对点模型,exchange的路由键必须和binding上指定的路由键完全匹配<br>
 * fanout:广播模式,只要是发送到binding的消息,都会发送到绑定的队列中<br>
 * topic:主题模式,根据路由键的匹配规则接收消息,类似于正则,其中#表示0个及以上字符,*表示单个字符<br
 * headers:匹配AMQP消息的header而不是路由键,和direct交换器一样,但是性能差,几乎不用
 * 
 * AmqpAdmin:RabbitMQ系统管理组件,负责exchange,queue,binding的增删改查
 * 
 * @author ParadiseWY
 * @date 2020-12-07 16:56:43
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}