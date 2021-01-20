package com.wy;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
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
 * {@link UserDetailsService#loadUserByUsername}:该方法定义登录时的具体行为
 * {@link UserDetails}:具体的用户实现类需要实现该接口,权限方法等需要在该类中添加<br>
 * {@link UserDetails#getAuthorities()}:角色权限方法等需要在该类中添加,角色都要添加ROLE_前缀,权限不需要添加
 * {@link EnableGlobalMethodSecurity}:开启Security安全管理<br>
 * {@link EnableGlobalMethodSecurity#prePostEnabled()}:是否开启{PreAuthorize PostAuthorize}注解,默认不开启
 * {@link PreAuthorize}:该注解用来管理角色,权限等,值为SpringEL表达式,解析规则{@link SecurityExpressionRoot}
 * ->角色比较:在写入角色时需要在角色之前添加ROLE_,但是使用的时候是直接使用角色即可
 * -->如写入时为ROLE_ADMIN,拦截的时候写ADMIN即可,hasAnyRole('ADMIN','USER')或hasRole('ADMIN')
 * ->权限比较:在放入权限时直接放入即可,使用的时候直接写一样的即可,即hasAnyAuthority('create')或hasAuthority('create')
 * {@link PostAuthorize}:该注解在方法执行完之后判断是否有权限,可以用returnObject表示返回值对象
 * 
 * {@link PreFilter}:对Collection类型的方法参数进行拦截,只能用在实现了Collection的类型上,数组也不行
 * {@link PreFilter#filterTarget()}:被拦截的形参名,若方法中只有一个参数,可以不指定
 * {@link PreFilter#value()}:对参数的处理,可以是SpringEl表达式,可以使用filterObject代表实际参数中的单个值
 * 
 * {@link PostFilter}:作用等同于{@link PreFilter},只不过是在方法执行完之后过滤,filterObject表示实际结果的单个值
 * 
 * {@link EnableGlobalMethodSecurity#securedEnabled()}:是否开启{@link Secured}注解,默认不开启
 * {@link Secured}:该注解功能和{@link PreAuthorize}类似,但是没有它全面,只能判断角色,不能判断权限,
 * 且在UserDetails中写入的角色和拦截时候的角色一样,如写入时是ROLE_ADMIN,则拦截时也要写ROLE_ADMIN
 * 
 * {@link EnableGlobalMethodSecurity#jsr250Enabled()}:是否开启jsr250相关注解,默认不开启.相关注解包括如下:
 * {@link RolesAllowed}:同{@link Secured};{@link DenyAll}:全部拒绝;{@link PermitAll}:全部允许
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