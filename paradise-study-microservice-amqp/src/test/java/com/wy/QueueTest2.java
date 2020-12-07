package com.wy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wy.rabbitmq.provider.Provider2_1;
import com.wy.rabbitmq.provider.Provider2_2;
import com.wy.rabbitmq.provider.Provider2_3;

/**
 * 测试不同模式的时候不能用同样的exchange
 * 
 * @author ParadiseWY
 * @date 2019年4月16日 下午3:25:00
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootTest(classes = Application.class)
public class QueueTest2 {

	@Autowired
	private Provider2_1 provider2_1;

	@Autowired
	private Provider2_2 provider2_2;

	@Autowired
	private Provider2_3 provider2_3;

	@Test
	public void test2() {
		provider2_1.sendMsg("this is a test21");
		provider2_2.sendMsg("this is a test22");
		provider2_3.sendMsg("this is a test23");
	}
}