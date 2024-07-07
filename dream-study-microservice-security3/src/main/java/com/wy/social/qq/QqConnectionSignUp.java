package com.wy.social.qq;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * 自定义Spring Social在登录服务提供商成功时,默认会跳到注册页面的行为
 * 
 * {@link ConnectionSignUp}:默认获得服务提供商access_token成功时的行为为跳转到本系统的登录页面
 * 
 * {@link ConnectionSignUp#execute()}:因为本地系统中没有服务提供商的用户信息,系统会默认强制跳到注册页面.
 * 重写该方法会在系统跳转之前默认在本地数据库中插入用户信息,此时将不再跳转登录页面
 * 
 * @auther 飞花梦影
 * @date 2019-09-26 12:21:15
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class QqConnectionSignUp implements ConnectionSignUp {

	@Override
	public String execute(Connection<?> connection) {
		// 通过社交用户信息默认创建用户并返回用户唯一标识,根据业务来实现
		return connection.getDisplayName();
	}
}