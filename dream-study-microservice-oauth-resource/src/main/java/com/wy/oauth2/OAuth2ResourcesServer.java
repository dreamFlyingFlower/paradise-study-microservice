package com.wy.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
		security.resourceId("oauth-resource").tokenStore(jwtTokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(config.getSecurity().getPermitAllSources()).permitAll().anyRequest()
				.authenticated().and().formLogin();
		// .antMatchers("/messages/**").access("#oauth2.hasScope('message.read')");
		// .loginProcessingUrl("/user/login").usernameParameter("username")
		// .passwordParameter("password").successHandler(clientLoginSuccessHandler)
		// .failureHandler(loginFailHandler).and().csrf().disable();
	}
}