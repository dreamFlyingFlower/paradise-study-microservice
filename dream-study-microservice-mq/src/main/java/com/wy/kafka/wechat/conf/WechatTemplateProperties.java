package com.wy.kafka.wechat.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "template")
public class WechatTemplateProperties {

	/**
	 * 模板文件
	 */
	private List<WechatTemplate> templates;

	/**
	 * 结果处理:0-文件获取 1-数据库获取 2-ES
	 */
	private int templateResultType;

	/**
	 * 模板结果地址
	 */
	private String templateResultFilePath;

	@Data
	public static class WechatTemplate {

		private String templateId;

		private String templateFilePath;

		private boolean active;
	}
}