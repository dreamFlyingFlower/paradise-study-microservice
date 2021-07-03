package com.wy.crl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 10:55:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Controller
@RequestMapping("oauth")
public class AuthorizationCrl {

	@Value("${messages.base-uri}")
	private String messagesBaseUri;

	@Autowired
	@Qualifier("messagingClientAuthCodeRestTemplate")
	private OAuth2RestTemplate messagingClientAuthCodeRestTemplate;

	@Autowired
	@Qualifier("messagingClientClientCredsRestTemplate")
	private OAuth2RestTemplate messagingClientClientCredsRestTemplate;

	@Autowired
	@Qualifier("messagingClientPasswordRestTemplate")
	private OAuth2RestTemplate messagingClientPasswordRestTemplate;

	@GetMapping(value = "/authorize", params = "grant_type=authorization_code")
	public String authorization_code_grant(Model model) {
		String[] messages = this.messagingClientAuthCodeRestTemplate.getForObject(this.messagesBaseUri, String[].class);
		model.addAttribute("messages", messages);
		return "index";
	}

	@GetMapping("/authorized") // registered redirect_uri for authorization_code
	public String authorized(Model model) {
		String[] messages = this.messagingClientAuthCodeRestTemplate.getForObject(this.messagesBaseUri, String[].class);
		model.addAttribute("messages", messages);
		return "index";
	}

	@GetMapping(value = "/authorize", params = "grant_type=client_credentials")
	public String client_credentials_grant(Model model) {
		String[] messages =
				this.messagingClientClientCredsRestTemplate.getForObject(this.messagesBaseUri, String[].class);
		model.addAttribute("messages", messages);
		return "index";
	}

	@PostMapping(value = "/authorize", params = "grant_type=password")
	public String password_grant(Model model, HttpServletRequest request) {
		ResourceOwnerPasswordResourceDetails passwordResourceDetails =
				(ResourceOwnerPasswordResourceDetails) this.messagingClientPasswordRestTemplate.getResource();
		passwordResourceDetails.setUsername(request.getParameter("username"));
		passwordResourceDetails.setPassword(request.getParameter("password"));
		String[] messages = this.messagingClientPasswordRestTemplate.getForObject(this.messagesBaseUri, String[].class);
		model.addAttribute("messages", messages);
		// Never store the user's credentials
		passwordResourceDetails.setUsername(null);
		passwordResourceDetails.setPassword(null);
		return "index";
	}
}