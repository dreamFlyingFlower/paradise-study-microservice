package com.wy.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 邮件配置
 * @author ParadiseWY
 * @date 2019年9月25日
 */
@Getter
@Setter
public class MailProperties {
	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;
}