package com.wy.json;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 若OAuth2AuthorizationService获取信息时出现安全问题,可以参照此类.
 * 
 * 因为开启了安全策略,只有部分类可以被反序列化,无法被反序列化的类都可以参照该类进行添加
 * 
 * 开启Web安全,应用在Web环境下:
 * 
 * <pre>
 * 1: 加载了WebSecurityConfiguration配置类, 配置安全认证策略
 * 2: 加载了AuthenticationConfiguration, 配置了认证信息
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-11-13 14:17:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class OAuth2AuthorizationServiceJackson {

	public OAuth2AuthorizationService auth2AuthorizationService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		/**
		 * 解决spring security oauth2 自定义user 用户登录 Jackson报错
		 * 
		 * The class with com.wy.entity.UserEntity and name of com.wy.entity.UserEntity
		 * is not in the allowlist. If you believe this class is safe to deserialize,
		 * please provide an explicit mapping using Jackson annotations or by providing
		 * a Mixin. If the serialization is only done by a trusted source, you can also
		 * enable default typing. See
		 * https://github.com/spring-projects/spring-security/issues/4370 for details
		 */
		JdbcOAuth2AuthorizationService service =
				new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
		JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper authorizationRowMapper =
				new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
		authorizationRowMapper.setLobHandler(new DefaultLobHandler());

		ObjectMapper objectMapper = new ObjectMapper();
		ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
		// Module类在java.lang包中也有,需要手动导入
		List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
		objectMapper.registerModules(securityModules);
		objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
		// 放入自定义的user类
		objectMapper.addMixIn(User.class, CustomizerUserMixin.class);
		authorizationRowMapper.setObjectMapper(objectMapper);

		service.setAuthorizationRowMapper(authorizationRowMapper);
		return service;
	}
}