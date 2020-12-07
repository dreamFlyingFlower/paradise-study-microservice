package com.wy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wy.rabbitmq.provider.Provider3;

/**
 *  测试不同模式的时候不能用同样的exchange
 * @author ParadiseWY
 * @date 2019年4月16日 下午3:25:00
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootTest(classes = Application.class)
public class QueueTest3 {
	@Autowired
	private Provider3 provider3;

	@Autowired

	@Test
	public void test3() {
		provider3.sendMsg("this is a test3");
	}
}