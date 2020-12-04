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