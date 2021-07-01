package com.wy.config;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * @apiNote 定义session失效时的事件
 * @author ParadiseWY
 * @date 2019年9月27日
 */
public class SessionExpiredStrategy implements SessionInformationExpiredStrategy{

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
			throws IOException, ServletException {
		event.getResponse().setContentType("application/json;charset=utf8");
		event.getResponse().getWriter().write("多用户同时登录");
	}
}