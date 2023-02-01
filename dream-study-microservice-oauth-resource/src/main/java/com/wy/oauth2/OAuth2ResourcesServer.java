package com.wy.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.wy.properties.ConfigProperties;

/**
 * OAuth2资源服务器,如果和认证服务器放在一起报异常,则分开放在不同的项目
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
	private TokenStore jwtTokenStore;

	@Override
	public void configure(ResourceServerSecurityConfigurer security) throws Exception {
		// 指定当前资源的id和存储方案
		security.resourceId("oauth-resource").tokenStore(jwtTokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// 白名单中的请求直接允许
				.antMatchers(config.getSecurity().getPermitAllSources()).permitAll().anyRequest().authenticated()
				// 指定不同请求方式访问资源所需要的权限,一般查询是read,其余是write
				.antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
				.antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
				// 指定特殊请求权限
				.antMatchers("/messages/**").access("#oauth2.hasScope('message.read')").and().headers()
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
				}).and().csrf().disable();
	}
}