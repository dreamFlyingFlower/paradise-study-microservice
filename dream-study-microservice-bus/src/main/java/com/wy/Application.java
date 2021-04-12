package com.wy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ContextIdApplicationContextInitializer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 客户端配置自动刷新,需要使用cloud-bus和mq或kafka,仍然需要添加{@link RefreshScope},看到42分钟,trace页面无法打开
 * 
 * @apiNote 添加依赖actuator,bus-amqp,安装rabbitmq,详见paradise-study-microservice/notes/MQ.md
 * 
 * @apiNote 手动刷新,关闭actuator的拦截,发送post请求到客户端ip:port/actuator/bus-refresh.
 *          若只想刷新某个client客户端,而不是全部都刷新,可以在url后添加destination=applictionname:port,
 *          也可以使用通配符,如destination=applictionname:**.
 *          但是该方法在集群中有bug,因为集群中的applicationname:port是相同的,这样并不会刷新.
 *          其实destination是一个applicaitonid,详见{@link ContextIdApplicationContextInitializer#initialize},
 *          在新的版本中可能destination会修改,需要去官方文档查看
 * 
 * @apiNote 自动刷新:需要在git配置的项目中添加webhooks,从项目的settings中进入->Webhooks->add webhook:
 *          payload url:添加客户端需要发送post请求的地址,如客户端ip:port/actuator/bus-refresh,
 *          只需要添加一个客户端地址即可,因为使用了amqp,其他client客户端会自动从amqp中后去信息.
 *          content-type:发送post请求的类型<br>
 *          secret:若访问有密码,可添加密码,没有密码可不填<br>
 *          选择发送的信息:可根据情况选择,只发送push事件,所有事件,自定义不发送事件,最后点击添加即可
 * 
 * @apiNote 自动刷新可以下载git专门的webhook在本地安装,可以确保安全,也可以在本地安装了git之后使用
 * 
 * @apiNote bus可以开启一个trace的页面,但是好像有bug还是缺少条件,暂时无法开启,可能需要在集群中注册
 * 
 * @apiNote 开启config的自动刷新,实际上是请求了config的客户端,然后客户端1再通知amqp说配置发生了变化,
 *          而amqp则通知其他客户端配置发生了变化,需要重新拉取配置,这里客户端1的职责就变了,不再是只接受消息,
 *          同时要发送消息到amqp,而其他客户端只接收消息,所有比较合理的设计应该是git自动刷新的地址填写config服务端,
 *          服务端中也加入bus-amqp,actuator,这样一旦配置改动,config服务端将需要刷新配置的消息写入到amqp中,
 *          其他客户端只用接收消息,从config服务端拉取最新的配置即可
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