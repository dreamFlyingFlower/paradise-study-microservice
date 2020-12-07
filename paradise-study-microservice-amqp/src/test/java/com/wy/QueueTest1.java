package com.wy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wy.rabbitmq.provider.Provider1;

/**
 * 测试不同模式的时候不能用同样的exchange
 * 
 * @author ParadiseWY
 * @date 2019年4月16日 下午2:57:57
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootTest(classes = Application.class)
public class QueueTest1 {

	@Autowired
	private Provider1 provider1;

	@Test
	public void test1() {
		provider1.sendMsg("this is a test1");
	}
}