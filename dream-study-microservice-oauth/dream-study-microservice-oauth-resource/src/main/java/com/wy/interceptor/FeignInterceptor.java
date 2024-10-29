//package com.wy.interceptor;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import dream.flying.flower.framework.web.helper.WebHelpers;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 如果涉及到微服务之间的调用,需要传递Token时可添加该类
// *
// * @author 飞花梦影
// * @date 2024-10-29 18:16:16
// * @git {@link https://github.com/dreamFlyingFlower}
// */
//@Slf4j
//public class FeignInterceptor implements RequestInterceptor {
//
//	@Override
//	public void apply(RequestTemplate template) {
//		// 业务逻辑 模拟认证逻辑
//		HttpServletRequest request = WebHelpers.getRequest();
//		String accessToken = request.getHeader("Authorization");
//		log.info("从Request中解析请求头:{}", accessToken);
//		// 设置token
//		template.header("Authorization", accessToken);
//	}
//}