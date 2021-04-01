package com.wy.activemq.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * publish:Topic模式消息发布者,若是消息发布之后,没有订阅者,那么该消息将会直接丢弃,并不会存储
 *
 * @author 飞花梦影
 * @date 2019-05-20 20:59:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class Producer2 {

	public static void main(String[] args) {
		Producer2 p = new Producer2();
		p.sendMsg("ceshi");
	}

	public void sendMsg(String msg) {
		// 连接工厂,用户名,密码,url地址
		ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.1.146:61616");
		// 连接.若是建立工厂的时候不传用户名和密码,可在创建连接的时候传值
		try (Connection conn = factory.createConnection();) {
			// 建立连接,消息的发送者不是必须启动连接,但是消费者必须启动连接
			// 因为在发送消息的时候会检查生产者是否启动,若未启动,会自动启动.若有特殊配置,建议配置完后启动
			conn.start();
			// 会话.参数为是否支持事务,如何确认消息处理
			// 一般批量处理数据时才会使用事务,单数据不需要使用事务
			// 当支持事务时,第2个参数默认无效,建立传递的数据是Session.SESSION_TRANSACTED
			// 当不支持事务时,第2个参数必须传,且必须有效
			// Session.AUTO_ACKNOWLEDGE:自动确认.消息的消费者处理完后自动确认.常用
			// Session.CLIENT_ACKNOWLEDGE:客户端手动确认,处理完消息后,手动确认
			// Session.DUPS_OK_CLIENT_ACKNOWLEDGE:有副本的客户端手动确认,即消息可多消费的时候使用,不推荐使用
			Session session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			// 目的地.通过连接对象,创建会话对象,必须绑定目的地
			Topic topic = session.createTopic("first-topic");
			// 消息发送者,发送消息的目的地可以在创建发送者的时候指定,也可以在发送的时候指定
			MessageProducer producer = session.createProducer(topic);
			// 消息对象
			TextMessage message = session.createTextMessage(msg);
			// MapMessage message = session.createMapMessage();
			// message.setString("username", "admin");
			// message.setString("password", "123456");
			// 使用producer发送消息到目的地中.若消息发送失败,则抛出异常
			producer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}