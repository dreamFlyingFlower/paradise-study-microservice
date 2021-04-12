//package com.wy.filter;
//
//import java.io.IOException;
//import java.util.Map;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.wy.properties.ResourceProperties;
//import com.wy.result.ResultException;
//import com.wy.utils.MapUtils;
//import com.wy.utils.StrUtils;
//import com.wy.ymls.ResourceYml;
//
///**
// * url拦截器,只有登录和下载资源不需要校验,其他都需要进行校验
// * @instruction 需要配合redis缓存使用
// * @author paradiseWy
// */
//@Order(1)
//@Configuration
//@ConfigurationProperties(prefix = "config.resource")
//public class ApiFilter extends OncePerRequestFilter {
//
//	@Autowired
//	private ResourceProperties resourceYml;
//	@Autowired
//	private RedisTemplate<Object, Object> redisTemplate;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//			FilterChain filterChain) throws ServletException, IOException {
//		if (request.getRequestURI().startsWith("/user/login")
//				|| request.getRequestURI().startsWith("/download/")) {
//			// 登录和下载资源放过
//			filterChain.doFilter(request, response);
//		} else {
//			// 从redis缓存中检验是否存在某个值,值从请求头的auth中来
//			if (resourceYml.isValidApi()) {
//				String auth = request.getHeader("");
//				if (StrUtils.isBlank(auth)) {
//					throw new ResultException("您还未登录,请登录");
//				}
//				Map<Object, Object> entity = redisTemplate.opsForHash().entries(auth);
//				if (MapUtils.isNotBlank(entity)) {
//					filterChain.doFilter(request, response);
//				} else {
//					throw new ResultException("您还未登录,请登录");
//				}
//			} else {
//				filterChain.doFilter(request, response);
//			}
//		}
//	}
//}