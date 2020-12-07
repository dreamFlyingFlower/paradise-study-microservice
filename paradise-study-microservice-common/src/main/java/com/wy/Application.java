package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 通用信息
 * 
 * {@link EnableScheduling}:使用Spring定时任务{@link Scheduled}需要开启该注解
 * {@link EnableAsync}:使用异步任务注解{@link Async}需要开启该注解
 * {@link EnableWebSecurity}:使用SpringSecurity时需要开启该注解,需要导入相关包
 * {@link EnableAspectJAutoProxy}:是否使用cglib作为动态代理的关键jar,默认不使用
 * 
 * @apiNote sharding数据库分库分表技术:https://blog.csdn.net/shijiemozujiejie/article/details/80786231
 *
 * @author ParadiseWY
 * @date 2020-12-05 23:58:47
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
