package com.wy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import zipkin2.server.internal.EnableZipkinServer;

/**
 * 通用信息
 * 
 * SpringBoot Actuator各种监控信息URL:ip:port/actuator/:
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
 * shutdown:优雅地关闭应⽤用程序,post请求,默认关闭,可http访问,jmx访问用jconsole
 * threaddump:查看线程情况,默认开启,可http访问,jmx访问用jconsole
 * heapdump:返回Jvm Heap Dump文件,格式为HPROF,默认开启,可以使用JDK自带的VisualVM打开文件查看内存快照
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
 * Prometheus:监控,块存储,了解即可,运维相关
 * 
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
 * 定制Bean,除了自动配置之外,一些其他途径:
 * 
 * <pre>
 * {@link InitializingBean}:初始化Bean时指定bean操作
 * {@link PostConstruct}:初始化Bean时指定bean操作,只能对方法使用
 * {@link DisposableBean}:bean实例被销毁时指定操作
 * {@link  PreDestroy}:bean实例被销毁时指定操作,只能对方法使用
 * {@link ApplicationContextAware}:可以获得{@link ApplicationContext},通过该类对bean进行操作
 * {@link BeanFactoryAware}:获得{@link BeanFactory},作用类似于{@link ApplicationContext}
 * {@link BeanNameAware}:只能设置bean的name
 * {@link ClassUtils#isPresent(String, ClassLoader)}:判断某个类是否存在,不限定在Spring上下文中
 * {@link ListableBeanFactory#containsBeanDefinition(String)}:判断指定名称的bean是否已定义
 * {@link ListableBeanFactory#getBeanNamesForType(Class)}:判断指定class的bean是否已定义
 * {@link BeanDefinitionRegistry#registerBeanDefinition()}:通过{@link GenericBeanDefinition}将某个类注入到Spring上下文
 * {@link SingletonBeanRegistry#registerSingleton()}:通过{@link DefaultSingletonBeanRegistry}注册单例的bean到Spring上下文中
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