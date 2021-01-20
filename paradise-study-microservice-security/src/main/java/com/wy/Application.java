package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * springsecurity默认的登录请求是/login,而且必须是post请求<br>
 * https://blog.csdn.net/abcwanglinyong/article/details/80981389<br>
 * https://blog.csdn.net/qq_29580525/article/details/79317969<br>
 * https://blog.csdn.net/XlxfyzsFdblj/article/details/82083443 实现动态权限控制<br>
 * {@link https://www.cnblogs.com/softidea/p/7068149.html}<br>
 * 
 * {@link EnableGlobalMethodSecurity}:开启Security安全管理<br>
 * {@link UserDetailsService#loadUserByUsername}:该方法定义登录时的具体行为
 * {@link UserDetails}:具体的用户实现类需要实现该接口,权限方法等需要在该类中添加
 * {@link EnableGlobalMethodSecurity#prePostEnabled()}:是否开启{@link PreAuthorize}注解功能
 * {@link PreAuthorize}:该注解用来管理角色,权限等,值为SpringEL表达式,解析规则{@link SecurityExpressionRoot}
 * ->角色比较:在放入角色时需要在角色之前添加ROLE_,但是使用的时候是直接使用角色即可
 * -->如放入时为ROLE_ADMIN,使用的时候写ADMIN即可,hasAnyRole('ADMIN')或hasRole('ADMIN')
 * ->权限比较:在放入权限时直接放入即可,使用的时候直接写一样的即可
 * 
 * {@link EnableRedisHttpSession}:通过redis开启session的集群共享功能,或者通过配置文件的sorttype配置
 * 
 * @author 飞花梦影
 * @date 2019-01-31 00:09:33
 * @git {@link https://github.com/mygodness100}
 */
@EnableRedisHttpSession
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}