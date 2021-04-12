package com.wy.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @apiNote 该类是第三方登录到授权服务器成功的自定义回调,和 LoginSuccessHandler 一样,只不过本类并不直接返回结果,
 *          而是在第三方登录成功之后自定义返回token等
 * @author ParadiseWY
 * @date 2019年9月29日
 */
public class ClientLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		super.onAuthenticationSuccess(request, response, authentication);
	}
}