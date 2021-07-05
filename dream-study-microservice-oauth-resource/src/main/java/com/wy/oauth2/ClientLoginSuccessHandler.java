//package com.wy.oauth2;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Collections;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.OAuth2Request;
//import org.springframework.security.oauth2.provider.TokenRequest;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.util.StringUtils;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.wy.exception.AuthException;
//import com.wy.lang.StrTool;
//
///**
// * 该类是第三方登录到授权服务器成功的自定义回调,和 LoginSuccessHandler 一样,只不过本类并不直接返回结果,
// * 而是在第三方登录成功之后自定义返回token
// * 
// * {@link BasicAuthenticationFilter#doFilterInternal}:参照从中获取请求头信息的代码
// * ->{@link BasicAuthenticationConverter#convert}:获取请求头信息
// * 
// * @author 飞花梦影
// * @date 2019-09-29 16:55:39
// * @git {@link https://github.com/dreamFlyingFlower}
// */
//@Configuration
//public class ClientLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//
//	@Autowired
//	private ClientDetailsService clientDetailsService;
//
//	@Autowired
//	private AuthorizationServerTokenServices authorizationServerTokenServices;
//
//	@Autowired
//	private ObjectMapper objectMapper;
//
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws ServletException, IOException {
//
//		String header = request.getHeader("Authorization");
//		if (header == null) {
//			throw new AuthException("request header error");
//		}
//		header = header.trim();
//		if (!StringUtils.startsWithIgnoreCase(header, "Basic")) {
//			throw new AuthException("request header error");
//		}
//
//		if (header.equalsIgnoreCase("Basic")) {
//			throw new BadCredentialsException("Empty basic authentication token");
//		}
//		byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
//		byte[] decoded;
//		try {
//			decoded = Base64.getDecoder().decode(base64Token);
//		} catch (IllegalArgumentException e) {
//			throw new BadCredentialsException("Failed to decode basic authentication token");
//		}
//
//		String token = new String(decoded, StandardCharsets.UTF_8);
//
//		// 用户名:密码,第三方登录中即为clientId和clientSecret
//		int delim = token.indexOf(":");
//
//		if (delim == -1) {
//			throw new BadCredentialsException("Invalid basic authentication token");
//		}
//
//		String username = token.substring(0, delim);
//		String password = token.substring(delim + 1);
//
//		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
//
//		if (null == clientDetails) {
//			throw new BadCredentialsException("Invalid basic authentication token");
//		}
//		if (!StrTool.equalsIgnoreCase(clientDetails.getClientSecret(), password)) {
//			throw new BadCredentialsException("clientId and clientSecret do not match");
//		}
//
//		// 自定义一个授权模式,之后再自定义该授权模式实现类即可
//		TokenRequest tokenRequest =
//				new TokenRequest(Collections.emptyMap(), username, clientDetails.getScope(), "custom");
//		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
//		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
//		OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
//		response.getWriter().write(objectMapper.writeValueAsString(oAuth2AccessToken));
//	}
//}