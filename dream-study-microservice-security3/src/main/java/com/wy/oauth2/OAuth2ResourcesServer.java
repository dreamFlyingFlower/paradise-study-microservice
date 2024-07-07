package com.wy.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import com.wy.properties.DreamSecurityProperties;
import com.wy.security.LoginFailureHandler;

/**
 * OAuth2资源服务器,和认证服务器一样,只需要一个注解即可,他们可以放在一个类上
 * 
 * 若直接使用SpringSecurity配置好的流程,可以不继承ResourceServerConfigurerAdapter,其他什么都不用动.
 * 若使用自定义的授权,则需要继承ResourceServerConfigurerAdapter,重写configure() 当使用授权码模式时,开启资源服务器,/oauth/authorize将不能访问
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 22:58:54
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourcesServer extends ResourceServerConfigurerAdapter {

	@Autowired
	private DreamSecurityProperties dreamSecurityProperties;

	@Autowired
	private ClientLoginSuccessHandler clientLoginSuccessHandler;

	@Autowired
	private LoginFailureHandler loginFailHandler;

	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable())
				.formLogin(customizer -> customizer.loginProcessingUrl("/user/login").usernameParameter("username")
						.passwordParameter("password").successHandler(clientLoginSuccessHandler)
						.failureHandler(loginFailHandler))
				.authorizeHttpRequests(
						customizer -> customizer.requestMatchers(dreamSecurityProperties.getPermitSources()).permitAll()
								.anyRequest().authenticated());
	}
}