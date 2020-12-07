package com.wy.activemq.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @description 单个消息发送者
 * @author ParadiseWy
 * @date 2019年5月20日 下午8:59:26
 * @git {@link https://github.com/mygodness100}
 */
public class Producer1 {

	public static void main(String[] args) {
		Producer1 p = new Producer1();
		p.sendMsg("ceshi");
	}

	public void sendMsg(String msg) {
		// 连接工厂,用户名,密码,url地址.
		// 若不开启安全验证,可用只带url的构造
		// ConnectionFactory fac1 = new
		// ActiveMQConnectionFactory("tcp://192.168.1.146:61616");
		// 若是开启安全验证,则用户名和密码必传.若未开启,可用另外的构造
		ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin",
				"tcp://192.168.1.146:61616");
		// 若是开启了activemq的集群,那么可以这么写
		// failover:失败转移,当任意节点宕机,自动转移
		// ConnectionFactory factory1 = new ActiveMQConnectionFactory("admin", "admin",
		// "failover:(tcp://192.168.1.146:61616,tcp://192.168.1.146:61617,tcp://192.168.1.146:61618)?Randomize=false");
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
			// Session.DUPS_OK_CLIENT_ACKNOWLEDGE:有副本的客户端手动确认,即消息可多消费的时候使用,不推荐
			Session session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			// 目的地.通过连接对象,创建会话对象,必须绑定目的地
			Queue queue = session.createQueue("first-mq");
			// 消息发送者,发送消息的目的地可以在创建发送者的时候指定,也可以在发送的时候指定
			MessageProducer producer = session.createProducer(queue);
			// 消息对象
			TextMessage message = session.createTextMessage(msg);
			// 使用producer发送消息到目的地中.若消息发送失败,则抛出异常
			producer.send(message);
			// 事务开启的时候,需要手动提交,即创建session的第一个参数,false则不需要写
			session.commit();
			// 消息,持久化方式(NON_PERSISTENT:只存内存,PERSISTENT),优先级(越大越优先),消息有效期,毫秒
			// producer.send(message, DeliveryMode.PERSISTENT, 1, 3000);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}