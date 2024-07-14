package com.wy.util;

import javax.servlet.http.HttpServletRequest;

import dream.flying.flower.lang.StrHelper;

public class WebUtils {

	private static ThreadLocal<String> ipThreadLocal = new ThreadLocal<>();

	private WebUtils() {}

	public static void setIp(String ip) {
		ipThreadLocal.set(ip);
	}

	public static String getIp() {
		return ipThreadLocal.get();
	}

	/**
	 * Retrieve client ip address
	 *
	 * @param request HttpServletRequest
	 * @return IP
	 */
	public static String retrieveClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (isUnAvailableIp(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (isUnAvailableIp(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (isUnAvailableIp(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private static boolean isUnAvailableIp(String ip) {
		return StrHelper.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
	}
}