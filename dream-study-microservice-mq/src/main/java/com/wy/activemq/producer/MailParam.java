package com.wy.activemq.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailParam {

	/**
	 * 发件人
	 */
	private String from;

	/**
	 * 收件人
	 */
	private String to;

	/**
	 * 主题
	 */
	private String subject;

	/**
	 * 邮件内容
	 */
	private String content;

	public MailParam(String to, String subject, String content) {
		this.to = to;
		this.subject = subject;
		this.content = content;
	}
}