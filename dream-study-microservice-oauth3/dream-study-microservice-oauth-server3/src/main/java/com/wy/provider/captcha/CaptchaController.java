package com.wy.provider.captcha;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpSession;

/**
 * 登录时添加验证码
 *
 * @author 飞花梦影
 * @date 2024-11-02 14:31:34
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Controller
public class CaptchaController {

	/**
	 * 获取验证码,需要在SpringSecurity配置里放行该接口
	 * 
	 * @param session
	 * @return
	 */
	@RequestBody
	@GetMapping("/getCaptcha")
	public Map<String, Object> getCaptcha(HttpSession session) {
		// 使用hutool-captcha生成图形验证码
		// 定义图形验证码的长、宽、验证码字符数、干扰线宽度
		ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(150, 40, 4, 2);
		// 这里应该返回一个统一响应类，暂时使用map代替
		Map<String, Object> result = new HashMap<>();
		result.put("code", HttpStatus.OK.value());
		result.put("success", true);
		result.put("message", "获取验证码成功.");
		result.put("data", captcha.getImageBase64Data());
		// 存入session中
		session.setAttribute("captcha", captcha.getCode());
		return result;
	}

	/**
	 * 若发生异常,则将异常写入到登录页面
	 * 
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/login")
	public String login(Model model, HttpSession session) {
		Object attribute = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (attribute instanceof AuthenticationException) {
			AuthenticationException exception = (AuthenticationException) attribute;
			model.addAttribute("error", exception.getMessage());
		}
		return "login";
	}
}