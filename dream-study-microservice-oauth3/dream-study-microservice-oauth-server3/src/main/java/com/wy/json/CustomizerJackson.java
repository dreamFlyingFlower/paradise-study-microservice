package com.wy.json;

import java.util.List;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import com.fasterxml.jackson.databind.Module;

import dream.flying.flower.framework.json.JsonHelpers;

/**
 * SpringSecurity中有很多类不在反序列化白名单中,需要自定义,不能使用通用的{@link JsonHelpers}
 * 
 * 该类根据情况使用,可以直接注入到Spring中,但可能会影响其他类的反序列化.builder可以直接从Spring容器中获得
 *
 * @author 飞花梦影
 * @date 2024-11-13 14:07:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CustomizerJackson {

	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> {
			// jackson反序列化安全白名单:需要被反序列化的类,反序列化目标类
			builder.mixIn(SecurityContext.class, SecurityContextImpl.class);
			List<Module> securityModules = SecurityJackson2Modules.getModules(CustomizerJackson.class.getClassLoader());
			securityModules.add(new OAuth2AuthorizationServerJackson2Module());
			builder.modulesToInstall(securityModules.toArray(new Module[securityModules.size()]));
		};
	}
}