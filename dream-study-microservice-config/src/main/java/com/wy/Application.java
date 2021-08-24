package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 统一配置管理服务端:可以集中管理配置,不同环境使用不同配置,最重要的是运行期间可以动态调整配置和自动刷新
 * 
 * 单个配置文件中的通用配置,官方文档:
 * {@link https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.core.logging.group}
 * 
 * 分布式动态配置:
 * 
 * <pre>
 * SpringCloudConfig为分布式系统外部化提供了ConfigServer和ConfigClient,且都实现了对
 * {@link SpringEnvironment}和{@link PropertySource}抽象的映射,因此,非常适合Spring应用程序
 * 
 * ConfigServer是可横向扩展,集中式的配置服务器,用于集中管理应用程序各个环境下的配置,默认使用git存储内容,
 * 也可以使用svn,本地文件系统,vault存储系统
 * ConfigClient是CloudServer的客户端,用于读取存储在CloudServer中的配置
 * 
 * {@link EnableConfigServer}:启用SpringCloudConfig的服务端,服务区添加即可
 * {@link RefreshScope}:客户端使用,需要使用动态配置的类上添加该注解才能让动态配置实时生效
 * 
 * 访问配置的形式,在ip:port或服务名后的资源链接,见paradise-study-microserver/notes/CloudConfig.md
 * 
 * 可实现动态实时配置的组件:zk,consul,cloud-config,nacos
 * </pre>
 * 
 * Spring读取配置文件的顺序,详见官方文档:
 * {@see https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config}
 * 
 * <pre>
 * 1.先从JAR内部读取,再读取外部的同级目录下的配置,后读取的属性覆盖前面的属性
 * 2.内部:classpath:/->classpath:/config/
 * 3.外部:和JAR同级目录配置文件->和JAR同级目录的config目录下的配置->/config子目录的直接子目录
 * 4.同目录配置文件顺序:bootstrap.yml->configserver->application->application-xxx
 * 5.在启动JAR时指定的配置优先级较高,会覆盖所有其他同名配置
 * 6.启动JAR时,在启动参数上指定读取的配置文件和配置文件路径,此时优先级是最高的,不可被覆盖:
 * 		指定配置文件:java -jar test.jar --spring.profiles.active=dev,config
 * 		指定配置文件目录:java -jar test.jar --spring.config.location=optional:classpath:/config/
 * 		指定特殊属性:java -jar test.jar --server.port=8080
 * 7.optional:classpath:可选,存在则读取,不存在则跳过
 * 8.spring.config.location:该参数指定读取的目录或文件,若是目录,必须以/结尾
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2020-12-04 09:29:28
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
@EnableConfigServer
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}