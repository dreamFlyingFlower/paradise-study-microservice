package com.wy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wy.rabbitmq.provider.Provider;

/**
 * 测试不同模式的时候不能用同样的exchange
 * 
 * @author ParadiseWY
 * @date 2019年4月16日 上午11:07:50
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootTest(classes = Application.class)
public class QueueTest {

	@Autowired
	private Provider provider;

	@Test
	public void test() {
		provider.sendMsg("this is a test");
	}
}