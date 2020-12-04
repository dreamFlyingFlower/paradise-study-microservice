package com.wy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一配置管理客户端
 * 
 * @apiNote 因为config客户端从git或其他地方拉取配置是需要时间的,而一般拉取的都是appliction-xxx.yml,
 *          如果在application.yml中配置git服务器,那么就会形成冲突,需要将拉取配置的地址写在bootstrap.yml中,
 *          因为bootstrap.yml的读取在application.yml之前,一般该文件需要手动新建<br>
 *          不管是本地配置或jar启动参数中有同名属性,都使用远程的配置中的属性;远程配置没有的才使用本地配置
 * 
 * @apiNote 手动配置刷新,config服务端不需要修改,在客户端需要刷新的类上添加{@link RefreshScope}注解,
 *          同时还需要添加actuator的依赖,在配置文件中开启refresh访问权限:<br>
 *          management.endpoints.web.exposure.include: health,info,refresh<br>
 *          post请求调用客户端ip:port/actuator/refresh等待刷新即可
 * 
 * @author ParadiseWY
 * @date 2020-12-04 09:29:28
 * @git {@link https://github.com/mygodness100}
 */
@RequestMapping
@SpringBootApplication
@RefreshScope
public class Application {

	@Value("${server.port}")
	String name;

	@Value("${server.context-path}")
	String contextPath;

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
		System.out.println(contextPath);
		System.out.println(charset);
		System.out.println(configName);
		return null;
	}
}