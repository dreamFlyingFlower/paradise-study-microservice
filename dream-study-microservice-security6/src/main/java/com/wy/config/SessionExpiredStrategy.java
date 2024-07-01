package com.wy.config;

import java.io.IOException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import jakarta.servlet.ServletException;

/**
 * 自定义定义session失效时的事件,参照{@link SimpleRedirectSessionInformationExpiredStrategy}
 *
 * @author 飞花梦影
 * @date 2019-09-27 21:25:31
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class SessionExpiredStrategy implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		event.getResponse().setContentType("application/json;charset=utf8");
		event.getResponse().getWriter().write("多用户同时登录");
	}
}