package com.wy.rabbitmq.netty;

import java.util.concurrent.LinkedBlockingDeque;

import com.alibaba.fastjson.JSONObject;

/**
 * 多线程模拟生产者和消费者
 * 
 * @author 飞花梦影
 * @date 2021-12-23 15:08:24
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class RabbitThread {

	private static LinkedBlockingDeque<JSONObject> msgs = new LinkedBlockingDeque<JSONObject>();

	public static void main(String[] args) {
		// 生产线程
		Thread producerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(1000);
						JSONObject data = new JSONObject();
						data.put("userId", "1234");
						// 存入消息
						msgs.offer(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "生产者");
		producerThread.start();
		// 消费者线程
		Thread consumerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						JSONObject data = msgs.poll();
						if (data != null) {
							System.out.println(Thread.currentThread().getName() + "," + data);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "消费者");
		consumerThread.start();
	}
}