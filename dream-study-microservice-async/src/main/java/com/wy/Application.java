package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 异步执行组件,需要配合xxl-job进行执行
 * 
 * 容器初始化bean完成后遍历所有方法,把有@AsyncExecute注解的方法缓存起来,方法运行时通过AOP切面发布事件,事务事件监听处理异步执行策略
 * 
 * TODO 未完成
 * 
 * @author 飞花梦影
 * @date 2024-05-16 11:13:25
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}