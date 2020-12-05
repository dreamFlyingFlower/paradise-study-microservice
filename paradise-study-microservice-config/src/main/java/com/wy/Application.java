package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 统一配置管理服务端:可以集中管理配置,不同环境使用不同配置,最重要的是运行期间可以动态调整配置和自动刷新
 * 
 * 分布式配置可以使用的实现:zk,consul,cloud-config
 * 
 * SpringCloudConfig为分布式系统外部化提供了ConfigServer和ConfigClient,且都实现了对SpringEnvironment
 * 和PropertySource抽象的映射,因此,非常适合Spring应用程序<br>
 * ConfigServer是可横向扩展,集中式的配置服务器,用于集中管理应用程序各个环境下的配置,默认使用git存储内容,<br>
 * 也可以使用svn,本地文件系统,vault存储系统<br>
 * ConfigClient是CloudServer个客户端,用于操作存储在CloudServer中的配置
 * 
 * {@link EnableConfigServer}:启用SpringClouddConfig的服务端,必须添加
 * 
 * @apiNote 访问配置的形式,在ip:port或服务名后的资源链接,见paradise-study-microserver/notes/CloudConfig.md
 * 
 * @apiNote Spring读取配置文件的顺序,先从外部读取,再读取内部,高优先级会覆盖低优先级属性,详见官方文档:
 *          外部:和jar包同级目录的config目录下的配置->和jar同级目录的配置
 *          内部:项目根目录下的config目录下的配置->项目根目录下的配置->classpath:/config->classpath:/
 *          同目录配置文件顺序:bootstrap.yml->configserver->application->application-xxx
 * 
 *          在启动jar时指定的配置优先级最高,会覆盖所有其他同名配置.<br>
 *          启动jar时,在启动参数上指定读取的配置文件和配置文件路径:<br>
 *          指定配置文件:java -jar test.jar --spring.profiles.active=dev,config<br>
 *          指定配置文件目录:java -jar test.jar --spring.config.location=/config
 * 
 * @author ParadiseWY
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