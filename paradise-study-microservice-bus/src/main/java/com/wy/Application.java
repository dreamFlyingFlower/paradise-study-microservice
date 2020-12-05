package com.wy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 客户端配置自动刷新,需要使用cloud-bus和mq或kafka,仍然需要添加{@link RefreshScope}
 * 
 * @apiNote 添加依赖actuator,bus-amqp,安装rabbitmq,详见paradise-study-microservice/notes/MQ.md
 * 
 * @apiNote 仍然可以手动刷新,关闭actuator的拦截,发送post请求到客户端ip:port/actuator/bus-refresh
 *          自动刷新:需要在git配置的项目中添加webhooks,从项目的settings中进入->Webhooks->add webhook:
 *          payload url:添加客户端需要发送post请求的地址,如客户端ip:port/actuator/bus-refresh,<br>
 *          content-type:发送post请求的类型<br>
 *          secret:若访问有密码,可添加密码,没有密码可不填<br>
 *          选择发送的信息:可根据情况选择,只发送push事件,所有事件,自定义不发送事件,最后点击添加即可
 * 
 * @apiNote 自动刷新可以下载git专门的webhook在本地安装,可以确保安全,也可以在本地安装了git之后使用
 * 
 * @author ParadiseWY
 * @date 2020-12-04 09:29:28
 * @git {@link https://github.com/mygodness100}
 */
@RequestMapping
@RefreshScope
@SpringBootApplication
public class Application {

	@Value("${server.port}")
	String name;

	@Value("${server.servlet.encoding.charset}")
	String charset;

	@Value("${spring.cloud.zookeeper.connect-string}")
	String configName;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		for (String string : args) {
			System.out.println(string);
		}
	}

	@ResponseBody
	@GetMapping("getConfig")
	public String getConfig() {
		System.out.println(name);
		System.out.println(charset);
		System.out.println(configName);
		return null;
	}
}