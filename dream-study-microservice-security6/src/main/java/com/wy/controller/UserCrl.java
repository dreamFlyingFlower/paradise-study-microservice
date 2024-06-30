package com.wy.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.wy.entity.User;
import com.wy.entity.UserSocial;
import com.wy.oauth2.AppSignUpUtils;
import com.wy.verify.VerifyHandler;
import com.wy.verify.VerifyHandlerFactory;

import dream.flying.flower.framework.core.helper.JwtHelpers;
import dream.flying.flower.result.Result;
import dream.flying.flower.result.ResultException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		// return "success";
	}

	@GetMapping("signup")
	public String signup() {
		return "signup";
	}

	/**
	 * 可直接将Authentication当作参数传入,前端不需要传任何参数
	 * 
	 * 使用JWT令牌的时候,额外添加的信息不会被authentication获取,需要使用JWT相关工具解析请求头
	 * 
	 * @param authentication
	 * @return
	 */
	@GetMapping("getAuthentication")
	public Result<?> getAuthentication(Authentication authentication) {
		return Result.ok(authentication);
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
			String token = JwtHelpers.encode("secretKey", params);
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
	 * 创建验证码,根据验证码类型不同,调用不同的 {@link VerifyHandler}接口实现
	 * 
	 * @param type 验证的类型
	 * @param request 请求
	 * @param response 响应
	 */
	@GetMapping("getCode/{type}")
	public void getCode(@PathVariable String type, HttpServletRequest request, HttpServletResponse response) {
		factory.getHandler(type).generate(new ServletWebRequest(request, response), type);
	}

	@Autowired
	private ProviderSignInUtils providerSignUtils;

	/**
	 * 通过providerSignUtils工具类拿到授权服务器返回的相关信息
	 * 
	 * @param request 请求
	 * @return
	 */
	@GetMapping("/social/user")
	public UserSocial getSocialUser(HttpServletRequest request) {
		Connection<?> connection = providerSignUtils.getConnectionFromSession(new ServletWebRequest(request));
		return UserSocial.builder().providerId(connection.getKey().getProviderId())
				.providerUserId(connection.getKey().getProviderUserId()).nickname(connection.getDisplayName())
				.socialimage(connection.getImageUrl()).build();
	}

	@Autowired
	private AppSignUpUtils appSignUpUtils;

	/**
	 * 该类为第三方登录为APP时调用
	 */
	@GetMapping("/social/app/signUp")
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public UserSocial socialAppSign(HttpServletRequest request) {
		Connection<?> connection = providerSignUtils.getConnectionFromSession(new ServletWebRequest(request));
		UserSocial userSocial = UserSocial.builder().providerId(connection.getKey().getProviderId())
				.providerUserId(connection.getKey().getProviderUserId()).nickname(connection.getDisplayName())
				.socialimage(connection.getImageUrl()).build();
		appSignUpUtils.saveData(new ServletWebRequest(request), connection.createData());
		return userSocial;
	}

	/**
	 * 注册,会将服务提供商的用户信息写入到数据库,该数据库为UserConnection
	 * 
	 * @param request 请求
	 * @param user 用户
	 */
	@PostMapping("register")
	public void register(HttpServletRequest request, User user) {
		String userId = user.getUsername();
		// 只有web登录时才使用providerSignUtils
		// providerSignUtils.doPostSignUp(userId, new ServletWebRequest(request));
		// app注册时需要使用appSignUpUtils
		appSignUpUtils.doPostSignUp(userId, new ServletWebRequest(request));
	}

	/**
	 * session失效时的接口地址
	 * 
	 * @return
	 */
	@GetMapping("session/invalid")
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public Result<?> sessionValid() {
		return Result.error();
	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	@PreAuthorize("@permissionServices.hasRole('ADMIN')")
	@PostAuthorize("returnObject.data.username == authenticaiton.name")
	public Result<?> test(User user) {
		return Result.ok(user);
	}

	/**
	 * 可以直接使用请求参数做校验,前面要带上#,多个条件用and或or
	 * 
	 * <pre>
	 * principal:登录时存储在内存中的用户信息,存什么,就取什么
	 * returnObject:返回值,固定写法
	 * </pre>
	 * 
	 * @param userId 用户编号
	 * @param user 用户信息
	 * @return
	 */
	@PreAuthorize("#userId<10 and principal.username.equals(#username) and #user.username.equals('abc')")
	@PostAuthorize("returnObject.data.username == authenticaiton.name")
	public Result<?> test1(Integer userId, String username, User user) {
		return Result.ok(user);
	}
}