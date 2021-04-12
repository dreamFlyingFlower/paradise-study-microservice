package com.wy.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wy.utils.ListUtils;

/**
 * token过滤器,验证token有效性
 *
 * @author ParadiseWY
 * @date 2020年4月8日 上午12:28:32
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// 直接放过/和/crsf
		if (request.getRequestURI().equals("/") || request.getRequestURI().equalsIgnoreCase("/crsf")) {
			chain.doFilter(request, response);
			return;
		}
		// 需要放过的url,必须SecurityConfig的WebSecurity的ignore中先配置才有效
		List<String> permitUrl = new ArrayList<>();
		// 放过其他web请求
		if (ListUtils.isNotBlank(permitUrl)) {
			AntPathMatcher pathMatcher = new AntPathMatcher();
			for (String pattern : permitUrl) {
				// 必须已经添加在WebSecurity的ignore列表中才有效
				if (pathMatcher.match(pattern, request.getRequestURI())) {
					chain.doFilter(request, response);
					return;
				}
			}
		}
		// token检查
		chain.doFilter(request, response);
	}
}