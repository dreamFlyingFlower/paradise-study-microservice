package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wy.lang.StrTool;
import com.wy.properties.ConfigProperties;

/**
 * 客户端请求验证API
 *
 * @author 飞花梦影
 * @date 2021-07-06 14:58:37
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Controller
public class AuthCrl {

	@Autowired
	private WebClient webClient;

	@Autowired
	private ConfigProperties config;

	@GetMapping(value = "/authorize", params = "grant_type=authorization_code")
	public String authorization_code_grant(Model model) {
		String[] messages = retrieveMessages("oauthServer-client-code");
		model.addAttribute("messages", messages);
		return "index";
	}

	@GetMapping("/authorized")
	public String authorized(Model model) {
		String[] messages = retrieveMessages("oauthServer-client-code");
		model.addAttribute("messages", messages);
		return "index";
	}

	@GetMapping(value = "/authorize", params = "grant_type=client_credentials")
	public String client_credentials_grant(Model model) {
		// String[] messages = retrieveMessages("oauthServer-client-credentials");
		String[] messages = retrieveMessage("oauthServer-client-credentials");
		model.addAttribute("messages", messages);
		return "index";
	}

	@PostMapping(value = "/authorize", params = "grant_type=password")
	public String password_grant(Model model) {
		String[] messages = retrieveMessages("oauthServer-client-password");
		model.addAttribute("messages", messages);
		return "index";
	}

	@Autowired
	private ObjectMapper objectMapper;

	private String[] retrieveMessage(String clientRegistrationId) {
		try {
			String result =
					this.webClient.get().uri(this.config.getAuth2Resource().getUrlMessageResource())
							.header("content-type", MediaType.APPLICATION_JSON_VALUE)
							.attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction
									.clientRegistrationId(clientRegistrationId))
							.retrieve().bodyToMono(String.class).block();
			if (StrTool.isNotBlank(result)) {
				return objectMapper.readValue(result, String[].class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String[] retrieveMessages(String clientRegistrationId) {
		try {
			String[] result =
					this.webClient.get().uri(this.config.getAuth2Resource().getUrlMessageResource())
							.attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction
									.clientRegistrationId(clientRegistrationId))
							.retrieve().bodyToMono(String[].class).block();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}