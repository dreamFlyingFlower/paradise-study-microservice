package com.wy.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * 生产者拦截器,和Java中不同,进出都是按照同样的顺序消费,而不是在出去的时候倒序消费,消费者拦截器也是一样
 * 
 * 自定义拦截器需要在配置中文件指定,多个用逗号分隔
 *
 * @author 飞花梦影
 * @date 2023-07-16 09:10:39
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class MyProducerInterceptor implements ProducerInterceptor<String, String> {

	@Override
	public void configure(Map<String, ?> configs) {

	}

	/**
	 * 该方法封装进KafkaProducer.send方法中,即运行在用户主线程中.Producer确保在消息被序列化以计算分区前调用该方法.
	 * 用户可以在该方法中对消息做任何操作,但最好保证不要修改消息所属的topic和分区,否则会影响目标分区的计算
	 * 
	 * @param record
	 * @return
	 */
	@Override
	public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
		return null;
	}

	/**
	 * 该方法会在消息被应答之前或消息发送失败时调用,并且通常都是在Producer回调逻辑触发之前.
	 * onAcknowledgement运行在Producer的IO线程中,因此不要在该方法中放入很重的逻辑,否则会拖慢Producer的消息发送效率
	 * 
	 * @param metadata
	 * @param exception
	 */
	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

	}

	/**
	 * 关闭Interceptor,主要用于执行一些资源清理工作
	 */
	@Override
	public void close() {

	}

}