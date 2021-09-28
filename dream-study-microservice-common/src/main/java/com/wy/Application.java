package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.bind.annotation.RequestMapping;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import zipkin2.server.internal.EnableZipkinServer;

/**
 * 通用信息
 * 
 * sharding数据库分库分表技术:https://blog.csdn.net/shijiemozujiejie/article/details/80786231
 * 
 * sleuth:链路追踪,主要用来测试系统中各部分的性能以及改善系统
 * 
 * zipkin:是slenth的web端,可在网页查看sleuth的链路,需要配置sleuth的服务器,需开启@EnableZipkinServer注解
 * 
 * SpringBoot Actuator各种监控信息URL:
 * 
 * <pre>
 * beans:显示容器器中的Bean列表,默认开启,可http访问,jmx访问用jconsole
 * caches:显示应⽤用中的缓存,默认开启,不可http访问,jmx访问用jconsole
 * conditions:显示配置条件的计算情况,默认开启,不可http访问,jmx访问用jconsole
 * configprops:显示{@link ConfigurationProperties}的信息,默认开启,不可http访问,jmx访问用jconsole
 * env:显示{@link ConfigurableEnvironment}中的属性,默认开启,不可http访问,jmx访问用jconsole
 * health:显示健康检查信息,默认开启,可http访问,jmx访问用jconsole
 * httptrace:显示HTTP Trace信息,默认开启,不可http访问,jmx访问用jconsole
 * info:显示设置好的应用信息,默认开启,可http访问,jmx访问用jconsole
 * loggers:显示并更新⽇日志配置,默认开启,不可http访问,jmx访问用jconsole
 * metrics:显示应用的度量信息,默认开启,不可http访问,jmx访问用jconsole
 * mappings:显示所有的{@link RequestMapping}信息,,默认开启,不可http访问,jmx访问用jconsole
 * scheduledtasks:显示应用的调度任务信息,默认开启,不可http访问,jmx访问用jconsole
 * shutdown:优雅地关闭应⽤用程序,默认关闭,可http访问,jmx访问用jconsole
 * threaddump:执行Thread Dump,默认开启,可http访问,jmx访问用jconsole
 * heapdump:返回Heap Dump文件,格式为HPROF,默认开启,需要其他插件支持
 * prometheus:返回可供Prometheus抓取的信息,默认开启,需要其他插件支持
 * </pre>
 * 
 * SpringBoot Actuator Admin以界面的方式显示Actuator的监控信息.分为服务端和客户端
 * 
 * <pre>
 * 服务端:将{@link EnableAdminServer}添加在启动类获其他注解类上,需要使用spring-boot-admin-starter-server依赖
 * 客户端:配置Admin服务端的地址,management.endpoints.web.exposure.include=*,添加spring-boot-admin-starter-client依赖
 * </pre>
 * 
 * 可执行Jar包,其中包含以下信息:
 * 
 * <pre>
 * META-INF/MANIFEST.MF:Jar描述
 * 		Main-Class:Jar启动类,org.springframework.boot.loader.JarLauncher
 * 		Start-Class:项目主类
 * org/springframework/boot/loader:Spring Boot Loader
 * BOOT-INF/classes:项目内容
 * BOOT-INF/lib:项目依赖
 * </pre>
 *
 * @author 飞花梦影
 * @date 2020-12-05 23:58:47
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
@EnableZipkinServer
@EnableAdminServer
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}