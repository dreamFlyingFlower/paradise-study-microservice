package com.wy.crl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.wy.properties.ConfigProperties;

/**
 * 测试调用4种认证授权模式
 * 
 * @auther 飞花梦影
 * @date 2021-07-03 10:55:37
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Deprecated
@Controller
@RequestMapping("oauth")
public class AuthorizationCrl {

	@Autowired
	@Qualifier("oauth2ClientCodeRestTemplate")
	private OAuth2RestTemplate oauth2ClientCodeRestTemplate;

	@Autowired
	@Qualifier("oauth2ClientCredsRestTemplate")
	private OAuth2RestTemplate oauth2ClientCredsRestTemplate;

	@Autowired
	@Qualifier("oauth2ClientPasswordRestTemplate")
	private OAuth2RestTemplate oauth2ClientPasswordRestTemplate;

	@Autowired
	private ConfigProperties config;

	@GetMapping(value = "/authorize", params = "grant_type=authorization_code")
	public String authorization_code_grant(Model model) {
		String[] messages = this.oauth2ClientCodeRestTemplate
				.getForObject(config.getAuth2Resource().getUrlMessageResource(), String[].class);
		model.addAttribute("messages", messages);
		return "index";
	}

	@GetMapping("/authorized") // registered redirect_uri for authorization_code
	public String authorized(Model model) {
		String[] messages = this.oauth2ClientCodeRestTemplate
				.getForObject(this.config.getAuth2Resource().getUrlMessageResource(), String[].class);
		model.addAttribute("messages", messages);
		return "index";
	}

	@GetMapping(value = "/authorize", params = "grant_type=client_credentials")
	public String client_credentials_grant(Model model) {
		String messages = this.oauth2ClientCredsRestTemplate
				.getForObject(this.config.getAuth2Resource().getUrlMessageResource(), String.class);
		model.addAttribute("messages", messages);
		return "index";
	}

	@PostMapping(value = "/authorize", params = "grant_type=password")
	public String password_grant(Model model, HttpServletRequest request) {
		ResourceOwnerPasswordResourceDetails passwordResourceDetails =
				(ResourceOwnerPasswordResourceDetails) this.oauth2ClientPasswordRestTemplate.getResource();
		passwordResourceDetails.setUsername(request.getParameter("username"));
		passwordResourceDetails.setPassword(request.getParameter("password"));
		String[] messages = this.oauth2ClientPasswordRestTemplate
				.getForObject(this.config.getAuth2Resource().getUrlMessageResource(), String[].class);
		model.addAttribute("messages", messages);
		// Never store the user's credentials
		passwordResourceDetails.setUsername(null);
		passwordResourceDetails.setPassword(null);
		return "index";
	}

	@GetMapping("getTest")
	public String getTest() {
		RestTemplate restTemplate = new RestTemplate();
		String string = restTemplate.getForObject("http://127.0.0.1:55100/oauthServer/oauth/token?"
				+ "client_id=guest&client_secret=guest&grant_type=client_credentials", String.class);
		System.out.println(string);
		return string;
	}
}