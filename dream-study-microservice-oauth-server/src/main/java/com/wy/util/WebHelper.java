package com.wy.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.wy.ConstLang;
import com.wy.lang.StrTool;
import com.wy.result.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * Web 工具类,可参考 {@link WebUtils}
 *
 * @author 飞花梦影
 * @date 2022-09-06 17:32:24
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
public class WebHelper {

	/**
	 * 获取Boolean参数
	 */
	public static Boolean getBoolean(String name) {
		return Convert.toBool(getRequest().getParameter(name));
	}

	/**
	 * 获取Boolean参数
	 */
	public static Boolean getBoolean(String name, Boolean defaultValue) {
		return Convert.toBool(getRequest().getParameter(name), defaultValue);
	}

	/**
	 * 获取请求头中的属性
	 * 
	 * @param name 属性名
	 * @return 属性值
	 */
	public static String getHeader(String name) {
		return getHeader(getRequest(), name);
	}

	/**
	 * 获取请求头中的属性
	 * 
	 * @param request 请求头
	 * @param name 属性名
	 * @return 属性值
	 */
	public static String getHeader(HttpServletRequest request, String name) {
		String value = request.getHeader(name);
		if (StrTool.isEmpty(value)) {
			return ConstLang.STR_EMPTY;
		}
		return urlDecode(value);
	}

	public static Map<String, String> getHeaders(HttpServletRequest request) {
		Map<String, String> map = new LinkedCaseInsensitiveMap<>();
		Enumeration<String> enumeration = request.getHeaderNames();
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				map.put(key, request.getHeader(key));
			}
		}
		return map;
	}

	/**
	 * 获取Integer参数
	 */
	public static Integer getInt(String name) {
		return Convert.toInt(getRequest().getParameter(name));
	}

	/**
	 * 获取Integer参数
	 */
	public static Integer getInt(String name, Integer defaultValue) {
		return Convert.toInt(getRequest().getParameter(name), defaultValue);
	}

	/**
	 * 获取String参数
	 */
	public static String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * 获取String参数
	 */
	public static String getParameter(String name, String defaultValue) {
		return Convert.toStr(getRequest().getParameter(name), defaultValue);
	}

	/**
	 * 获取request
	 */
	public static HttpServletRequest getRequest() {
		try {
			return getRequestAttributes().getRequest();
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 获得ServletRequestAttributes
	 * 
	 * @return ServletRequestAttributes
	 */
	public static ServletRequestAttributes getRequestAttributes() {
		try {
			RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
			return (ServletRequestAttributes) attributes;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 获取response
	 */
	public static HttpServletResponse getResponse() {
		try {
			return getRequestAttributes().getResponse();
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 获取session
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 是否是Ajax异步请求
	 * 
	 * @param request
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		if (accept != null && accept.contains("application/json")) {
			return true;
		}

		String xRequestedWith = request.getHeader("X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
			return true;
		}

		String uri = request.getRequestURI();
		if (StrTool.containsAny(uri.toLowerCase(), ".json", ".xml")) {
			return true;
		}

		String ajax = request.getParameter("__ajax");
		return StrTool.containsAny(ajax.toLowerCase(), "json", "xml");
	}

	/**
	 * 将结果渲染到客户端
	 * 
	 * @param response 响应
	 * @param object 待渲染的对象
	 */
	public static void render(HttpServletResponse response, Object object) {
		try {
			response.setStatus(200);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(ConstLang.DEFAULT_CHARSET_NAME);
			response.getWriter().print(object);
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 内容编码
	 * 
	 * @param str 内容
	 * @return 编码后的内容
	 */
	public static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, ConstLang.DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			return ConstLang.STR_EMPTY;
		}
	}

	/**
	 * 内容解码
	 * 
	 * @param str 内容
	 * @return 解码后的内容
	 */
	public static String urlDecode(String str) {
		try {
			return URLDecoder.decode(str, ConstLang.DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			return ConstLang.STR_EMPTY;
		}
	}

	public static void write(HttpServletResponse response, Result<?> result) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		JacksonHelper.getInstance().writeValue(response.getOutputStream(), result);
	}

	public static <T> void write(HttpServletResponse response, T data) throws IOException {
		write(response, Result.ok(data));
	}

	public static void writeError(HttpServletResponse response) throws IOException {
		write(response, Result.error());
	}

	public static void writeOk(HttpServletResponse response) throws IOException {
		write(response, Result.ok());
	}
}