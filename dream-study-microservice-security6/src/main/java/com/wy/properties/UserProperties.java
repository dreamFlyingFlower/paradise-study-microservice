package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 所有自定义配置的总配置
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "user")
public class UserProperties {

	private MailProperties mail = new MailProperties();

	private FilterProperties filter = new FilterProperties();

	private SocialProperties social = new SocialProperties();

	private DreamSecurityProperties security = new DreamSecurityProperties();

	private VerifyProperties verify = new VerifyProperties();
}