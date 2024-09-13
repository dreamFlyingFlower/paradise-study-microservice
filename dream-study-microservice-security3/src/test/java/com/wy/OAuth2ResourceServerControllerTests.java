package com.wy;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wy.config.OAuth2ResourceServerSecurityConfiguration;
import com.wy.controller.OAuth2ResourceServerController;

/**
 * ResourceServer Mock测试
 *
 * @author 飞花梦影
 * @date 2024-09-13 10:46:09
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@WebMvcTest(OAuth2ResourceServerController.class)
@Import(OAuth2ResourceServerSecurityConfiguration.class)
public class OAuth2ResourceServerControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void indexGreetsAuthenticatedUser() throws Exception {
		// @formatter:off
		this.mockMvc.perform(get("/").with(jwt().jwt((jwt) -> jwt.subject("ch4mpy"))))
				.andExpect(content().string(is("Hello, ch4mpy!")));
		// @formatter:on
	}

	@Test
	void messageCanBeReadWithScopeMessageReadAuthority() throws Exception {
		// @formatter:off
		this.mockMvc.perform(get("/message").with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read"))))
				.andExpect(content().string(is("secret message")));

		this.mockMvc
				.perform(get("/message").with(jwt().authorities(new SimpleGrantedAuthority(("SCOPE_message:read")))))
				.andExpect(content().string(is("secret message")));
		// @formatter:on
	}

	@Test
	void messageCanNotBeReadWithoutScopeMessageReadAuthority() throws Exception {
		// @formatter:off
		this.mockMvc.perform(get("/message").with(jwt())).andExpect(status().isForbidden());
		// @formatter:on
	}

	@Test
	void messageCanNotBeCreatedWithoutAnyScope() throws Exception {
		// @formatter:off
		this.mockMvc.perform(post("/message").content("Hello message").with(jwt())).andExpect(status().isForbidden());
		// @formatter:on
	}

	@Test
	void messageCanNotBeCreatedWithScopeMessageReadAuthority() throws Exception {
		// @formatter:off
		this.mockMvc
				.perform(post("/message").content("Hello message")
						.with(jwt().jwt((jwt) -> jwt.claim("scope", "message:read"))))
				.andExpect(status().isForbidden());
		// @formatter:on
	}

	@Test
	void messageCanBeCreatedWithScopeMessageWriteAuthority() throws Exception {
		// @formatter:off
		this.mockMvc
				.perform(post("/message").content("Hello message")
						.with(jwt().jwt((jwt) -> jwt.claim("scope", "message:write"))))
				.andExpect(status().isOk())
				.andExpect(content().string(is("Message was created. Content: Hello message")));
		// @formatter:on
	}

	@Test
	@WithMockJwt("classpath:validjwt.json")
	void indexGreetsAuthenticatedUserWithMockJwt() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(content().string(is("Hello, ch4mpy!")));
	}

	@Test
	@WithMockJwt(value = "classpath:validjwt.json", authorities = "SCOPE_message:read")
	void messageCanBeReadWithScopeMessageReadAuthorityWithMockJwt() throws Exception {
		// @formatter:off
		this.mockMvc.perform(get("/message")).andExpect(content().string(is("secret message")));
		// @formatter:on
	}

	@Test
	@WithMockJwt(value = "classpath:validjwt.json", authorities = "SCOPE_message:read")
	void messageCanNotBeCreatedWithScopeMessageReadAuthorityWithMockJwt() throws Exception {
		// @formatter:off
		this.mockMvc.perform(post("/message").content("Hello message")).andExpect(status().isForbidden());
		// @formatter:on
	}

	@Retention(RetentionPolicy.RUNTIME)
	@WithSecurityContext(factory = JwtFactory.class)
	@interface WithMockJwt {

		String value();

		String[] authorities() default {};

	}

	static class JwtFactory implements WithSecurityContextFactory<WithMockJwt> {

		private final ResourceLoader resourceLoader = new DefaultResourceLoader();

		private final ObjectMapper mapper = new ObjectMapper();

		@SuppressWarnings("unchecked")
		@Override
		public SecurityContext createSecurityContext(WithMockJwt annotation) {
			Resource jwtString = this.resourceLoader.getResource(annotation.value());
			try (InputStream in = jwtString.getInputStream()) {
				Map<String, Object> claims = (Map<String, Object>) this.mapper.readValue(in, Map.class);
				Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claims((c) -> c.putAll(claims)).build();
				Collection<GrantedAuthority> authorities = Stream.of(annotation.authorities())
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
				return new SecurityContextImpl(new JwtAuthenticationToken(jwt, authorities));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

	}
}