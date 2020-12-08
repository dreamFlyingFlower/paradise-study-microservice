package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * springsecurity默认的登录请求是/login,而且必须是post请求
 * https://blog.csdn.net/abcwanglinyong/article/details/80981389
 * https://blog.csdn.net/qq_29580525/article/details/79317969
 * https://blog.csdn.net/XlxfyzsFdblj/article/details/82083443 实现动态权限控制
 * {@link https://www.cnblogs.com/softidea/p/7068149.html}
 * 
 * 实现session的redis共享,可实现集群,使用EnableRedisHttpSession注解, 配置redis地址,可简单实现
 * 
 * @apiNote EnableRedisHttpSession:通过redis开启session的集群共享功能,或者通过配置文件的sorttype配置
 * @author paradiseWy
 * @date 2019年1月31日 下午12:09:33
 */
@EnableRedisHttpSession
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}