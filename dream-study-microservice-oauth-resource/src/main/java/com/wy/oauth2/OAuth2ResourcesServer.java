package com.wy.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

import com.wy.handler.CustomizeAccessDeniedHandler;
import com.wy.handler.CustomizeAuthenticationEntryHandler;
import com.wy.properties.ConfigProperties;

/**
 * OAuth2资源服务器,如果和认证服务器放在一起报异常,则分开放在不同的项目
 * 
 * 在页面访问 http://localhost:55200/oauthResource/test/test1 可直接访问
 * 
 * 在页面访问http://localhost:55200/oauthResource/user/getAuthenticaiton?access_token=需要加上从认证服务器获得的token才能访问
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:26:41
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourcesServer extends ResourceServerConfigurerAdapter {

	@Autowired
	private ConfigProperties config;

	@Autowired
	private CustomizeAccessDeniedHandler selfAccessDeniedHandler;

	@Autowired
	private CustomizeAuthenticationEntryHandler selfAuthenticationEntryHandler;

	/**
	 * 定义资源服务器向远程认证服务器请求,进行token校验等
	 * 
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// 该情况适用于非jwt请求访问,直接远程访问认证服务器来鉴定access_token是否存在,有效,是否在有效期
		// 定义token服务对象(token校验就应该靠token服务对象)
		RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		// 校验端点/接口设置
		remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:55100/oauthServer/oauth/check_token");
		// 或者使用resttemplate进行远程访问,认证服务必须进行了注册
		// remoteTokenServices.setRestTemplate(new RestTemplate());
		// remoteTokenServices.setCheckTokenEndpointUrl("http://dream-study-microservice-oauth-server/oauthServer/oauth/check_token");
		// 携带客户端id和客户端安全码
		remoteTokenServices.setClientId("client_id");
		remoteTokenServices.setClientSecret("guest");
		// 设置当前资源服务的资源id
		resources.resourceId("oauth-resource").tokenServices(remoteTokenServices)
				// 自定义权限验证失败方式
				.accessDeniedHandler(selfAccessDeniedHandler).authenticationEntryPoint(selfAuthenticationEntryHandler);
	}

	/**
	 * 同SpringSecurity中的方法,过滤某些请求需要验证,某些不需要验证token
	 * 
	 * @param http
	 * @throws Exception
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(authorize -> authorize
				// 白名单中的请求直接允许
				.antMatchers(config.getSecurity().getPermitAllSources()).permitAll().antMatchers("/test/**").permitAll()
				// 指定不同请求方式访问资源所需要的权限,一般查询是read,其余是write
				// .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
				// .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
				// .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
				// .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
				// .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
				// 所有指定的URL都需要验证
				.antMatchers("/user/**").authenticated()
				// 指定特殊请求权限
				.antMatchers("/messages/**").access("#oauth2.hasScope('message.read')")
				// 其他请求都需要进行认证
				.anyRequest().authenticated())
				// 禁用session
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// 指定请求头参数
				.headers(header -> header
						// 指定请求头
						.addHeaderWriter((request, response) -> {
							// 允许跨域
							response.addHeader("Access-Control-Allow-Origin", "*");
							// 如果是跨域的预检请求,则原封不动向下传达请求头信息
							if (request.getMethod().equals("OPTIONS")) {
								response.setHeader("Access-Control-Allow-Methods",
										request.getHeader("AccessControl-Request-Method"));
								response.setHeader("Access-Control-Allow-Headers",
										request.getHeader("AccessControl-Request-Headers"));
							}
						}))
				.csrf(csrf -> csrf.disable());
	}
}