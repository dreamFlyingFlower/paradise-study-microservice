package com.wy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.wy.properties.ConfigProperties;

/**
 * Security配置类
 *
 * @author 飞花梦影
 * @date 2021-07-02 16:36:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ConfigProperties config;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(config.getSecurity().getPermitAllSources()).permitAll().anyRequest()
				.authenticated().and().formLogin();
	}

	@Bean
	public UserDetailsService users() throws Exception {
		User.UserBuilder users = User.builder();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(
				users.username("user1").password(passwordEncoder().encode("password")).roles("USER").build());
		manager.createUser(
				users.username("admin").password(passwordEncoder().encode("123456")).roles("USER", "ADMIN").build());
		return manager;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}