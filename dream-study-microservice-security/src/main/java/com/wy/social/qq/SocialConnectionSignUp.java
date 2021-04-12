package com.wy.social.qq;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * @apiNote 自定义social在登录第三方服务成功时,因为本地系统中没有注册信息,会强制跳到注册页面
 *          重写该类可以再跳转之前默认在数据库中插入用户信息,此时将不再跳转登录页面
 * @author ParadiseWY
 * @date 2019年9月26日
 */
@Component
public class SocialConnectionSignUp implements ConnectionSignUp {

	@Override
	public String execute(Connection<?> connection) {
		// 通过社交用户信息默认创建用户并返回用户唯一标识,根据业务来实现
		return connection.getDisplayName();
	}
}