package com.wy.config.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wy.common.ConstSecurity;
import com.wy.properties.DreamSecurityProperties;

import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.framework.core.helper.JwtHelpers;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * token过滤器,验证token有效性
 *
 * @author 飞花梦影
 * @date 2023-02-02 10:03:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final DreamSecurityProperties dreamSecurityProperties;

	private final UserDetailsService userDetailsService;

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
		if (ListHelper.isNotEmpty(permitUrl)) {
			AntPathMatcher pathMatcher = new AntPathMatcher();
			for (String pattern : permitUrl) {
				// 必须已经添加在WebSecurity的ignore列表中才有效
				if (pathMatcher.match(pattern, request.getRequestURI())) {
					chain.doFilter(request, response);
					return;
				}
			}
		}

		// 从请求头中获取认证信息
		String authHeader = request.getHeader(ConstSecurity.HEADER_AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith(ConstSecurity.HEADER_AUTHORIZATION_BEARER)) {
			chain.doFilter(request, response);
			return;
		}
		String jwt = authHeader.substring(7);
		// 从token中解析出username
		String username =
				JwtHelpers.parseClaim(dreamSecurityProperties.getJwtSecurityKey(), jwt, claim -> claim.getIssuer());
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// 根据jwt解析出来的username,获取数据库中的用户信息,封装UserDetails对象
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			// TODO 此处token有效性可以从redis或数据库中获取
			Boolean isTokenValid = true;
			if (!JwtHelpers.isExpired(dreamSecurityProperties.getJwtSecurityKey(), jwt)
					&& username.equals(userDetails.getUsername()) && isTokenValid) {
				// TODO 如果令牌有效,封装一个UsernamePasswordAuthenticationToken对象
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userDetails,
								// 用户凭证
								null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// 更新安全上下文的持有用户
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			chain.doFilter(request, response);
		}

		chain.doFilter(request, response);
	}
}