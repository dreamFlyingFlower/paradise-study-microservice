package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.InfoProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import zipkin.server.internal.EnableZipkinServer;

/**
 * 通用信息
 * 
 * {@link EnableScheduling}:使用Spring定时任务{@link Scheduled}需要开启该注解
 * {@link EnableAsync}:使用异步任务注解{@link Async}需要开启该注解
 * {@link EnableWebSecurity}:使用SpringSecurity时需要开启该注解,需要导入相关包
 * {@link EnableAspectJAutoProxy}:是否使用cglib作为动态代理的关键jar,默认不使用
 * 
 * Actuator:对集群中所有的组件,配置等信息进行监控,需要在配置文件中暴露相应的url,在网页访问相应url可显示信息:<br>
 * actuator/autoconfig,actuator/configprops:所有自动配置信息,所有配置属性<br>
 * actuator/auditevents:审计事件<br>
 * actuator/beans,actuator/mappings:所有bean的信息.应用@RequestMapping映射路径<br>
 * actuator/dump,actuator/env:线程状态信息,当前环境信息<br>
 * actuator/health,actuator/metrics:应用健康状况,应用各项指标<br>
 * actuator/info:应用信息,非程序应用信息.该信息是配置在info配置中的信息以及继承{@link InfoProperties}的信息<br>
 * actuator/shutdown:关闭当前应用(默认关闭),需要发送post请求到客户端关闭应用,实现优雅关机<br>
 * actuator/trace:追踪信息,最新的http请求
 * 
 * 自定义一个健康状态检测器:<br>
 * 需要实现{@link HealthIndicator}接口,指示器的名称只能是xxxHealthIndicator,并将指示器加入到Spring上下文中
 * 
 * sharding数据库分库分表技术:https://blog.csdn.net/shijiemozujiejie/article/details/80786231
 * 
 * sleuth:链路追踪,主要用来测试系统中各部分的性能以及改善系统
 * 
 * zipkin:是slenth的web端,可在网页查看sleuth的链路,需要配置sleuth的服务器,需开启@EnableZipkinServer注解
 *
 * @author ParadiseWY
 * @date 2020-12-05 23:58:47
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
@EnableZipkinServer
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}