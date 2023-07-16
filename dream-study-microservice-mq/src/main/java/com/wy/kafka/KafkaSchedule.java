package com.wy.kafka;import org.apache.kafka.clients.consumer.ConsumerRecord;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.context.annotation.Bean;import org.springframework.kafka.annotation.KafkaListener;import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;import org.springframework.kafka.config.KafkaListenerEndpointRegistry;import org.springframework.kafka.core.ConsumerFactory;import org.springframework.scheduling.annotation.EnableScheduling;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;/** * 定时启动,停止监听器.默认情况下,当消费者项目启动的时候,监听器就开始监听消费发送到指定topic的消息 * * 设置定时启动,停止消息监听器 *  * <pre> * 1.禁止监听器自启动 * 2.创建两个定时任务,一个用来在指定时间点启动定时器,另一个在指定时间点停止定时器 * 3.新建一个定时任务类,用注解@EnableScheduling声明,KafkaListenerEndpointRegistry * 4.在SpringIO中已经被注册为Bean,直接注入,设置禁止KafkaListener自启动 * </pre> *  * @author 飞花梦影 * @date 2021-04-24 18:24:51 * @git {@link https://github.com/dreamFlyingFlower} */@EnableScheduling@Componentpublic class KafkaSchedule {	/**	 * {@link KafkaListener}:该注解标注的方法不会在IOC容器中被注册为Bean,而是会被注册在KafkaListenerEndpointRegistry中,	 * 而KafkaListenerEndpointRegistry在SpringIOC中已经被注册为Bean	 */	@Autowired	private KafkaListenerEndpointRegistry registry;	@Autowired	private ConsumerFactory<String, Object> consumerFactory;	/**	 * 监听器容器工厂,设置禁止KafkaListener自启动	 * 	 * @return Kafka监听工厂	 */	@Bean	public ConcurrentKafkaListenerContainerFactory<String, Object> delayContainerFactory() {		ConcurrentKafkaListenerContainerFactory<String, Object> container =				new ConcurrentKafkaListenerContainerFactory<String, Object>();		container.setConsumerFactory(consumerFactory);		// 禁止KafkaListener自启动		container.setAutoStartup(false);		return container;	}	/**	 * 监听器	 * 	 * @param record 消息记录	 */	@KafkaListener(id = "timingConsumer", topics = "testTopic1", containerFactory = "delayContainerFactory")	public void onMessage1(ConsumerRecord<?, ?> record) {		System.out.println("消费成功:" + record.topic() + "-" + record.partition() + "-" + record.value());	}	/**	 * 设置一个12点0分0秒启动的监听器,当项目启动时,若没到时间,将不会监听消息	 */	@Scheduled(cron = "0 0 12 * * ? ")	public void startListener() {		System.out.println("启动监听器...");		// "timingConsumer"是@KafkaListener注解后面设置的监听器ID,标识这个监听器		if (!registry.getListenerContainer("timingConsumer").isRunning()) {			registry.getListenerContainer("timingConsumer").start();		}		// registry.getListenerContainer("timingConsumer").resume();	}	/**	 * 定时停止监听器,到12点45分就不再监听消息	 */	@Scheduled(cron = "0 45 12 * * ? ")	public void shutDownListener() {		System.out.println("关闭监听器...");		registry.getListenerContainer("timingConsumer").pause();	}}