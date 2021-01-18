package com.wy.crl;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.google.common.collect.ImmutableMap;
import com.wy.entity.UserSocial;
import com.wy.jwt.S_Jwt;
import com.wy.entity.User;
import com.wy.result.Result;
import com.wy.result.ResultException;
import com.wy.verify.VerifyHandler;
import com.wy.verify.VerifyHandlerFactory;

@RestController
@RequestMapping("user")
public class UserCrl {

	@PostMapping("login1")
	public String login() {
		System.out.println("denglu");
		return "login";
	}

	@GetMapping("error")
	public String error() {
		// 获得相关验证信息
		// Authentication authentication =
		// SecurityContextHolder.getContext().getAuthentication();
		throw new ResultException("fdsfds");
//		return "success";
	}

	@GetMapping("signup")
	public String signup() {
		return "signup";
	}

	/**
	 * JWT鉴权测试
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @param response 响应
	 * @return 是否成功
	 */
	@GetMapping("login2")
	public Result<?> login(String username, String password, HttpServletResponse response) {
		if (Objects.equals("admin", username) && Objects.equals("123456", password)) {
			Map<String, String> params = ImmutableMap.of("test1", "test1", "test2", "test2");
			String token = S_Jwt.genericToken(params);
			Cookie cookie = new Cookie("token", token);
			cookie.setPath("/");
			response.addCookie(cookie);
			return Result.ok();
		}
		return Result.error();
	}

	/**
	 * JWT获取token
	 * 
	 * @param token 从前端回传的token
	 * @return token值
	 */
	@GetMapping("getToken")
	public Result<?> getToken(@CookieValue("token") String token) {
		System.out.println(token);
		return Result.ok();
	}

	@Autowired
	private VerifyHandlerFactory factory;

	/**
	 * 创建验证码，根据验证码类型不同，调用不同的 {@link VerifyHandler}接口实现
	 * 
	 * @param type 验证的类型
	 * @param request 请求
	 * @param response 响应
	 */
	@GetMapping("getCode/{type}")
	public void getCode(@PathVariable String type, HttpServletRequest request,
			HttpServletResponse response) {
		factory.getHandler(type).generate(new ServletWebRequest(request, response), type);
	}

	@Autowired
	private ProviderSignInUtils providerSignUtils;

	/**
	 * 通过providerSignUtils工具类拿到授权服务器返回的相关信息
	 * @param request 请求
	 * @return
	 */
	@GetMapping("/social/user")
	public UserSocial getSocialUser(HttpServletRequest request) {
		Connection<?> connection = providerSignUtils
				.getConnectionFromSession(new ServletWebRequest(request));
		return UserSocial.builder().providerId(connection.getKey().getProviderId())
				.providerUserId(connection.getKey().getProviderUserId())
				.nickname(connection.getDisplayName()).socialimage(connection.getImageUrl())
				.build();
	}
	
	/**
	 * 注册,会将信息写入到数据库
	 * @param request 请求
	 * @param user 用户
	 */
	@PostMapping("register")
	public void register(HttpServletRequest request,User user) {
		String userId=  user.getUsername();
		providerSignUtils.doPostSignUp(userId, new ServletWebRequest(request));
	}
	
	/**
	 * session失效时的接口地址
	 * @return
	 */
	@GetMapping("session/invalid")
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public Result<?> sessionValid() {
		return Result.error();
	}
}