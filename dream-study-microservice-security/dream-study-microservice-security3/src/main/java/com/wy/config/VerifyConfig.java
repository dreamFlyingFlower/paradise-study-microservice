package com.wy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wy.verify.VerifyGenerator;
import com.wy.verify.code.ImageVerifyGenerator;
import com.wy.verify.sms.DefaultSmsSend;
import com.wy.verify.sms.SmsSend;
import com.wy.verify.sms.SmsVerifyGenerator;

/**
 * @apiNote 验证配置
 * @author ParadiseWY
 * @date 2019年9月24日
 */
@Configuration
public class VerifyConfig {

	@Bean
	@ConditionalOnMissingBean(name = "imageVerifyGenerator")
	VerifyGenerator imageVerifyGenerator() {
		return new ImageVerifyGenerator();
	}

	@Bean
	@ConditionalOnMissingBean(name = "smsVerifyGenerator")
	VerifyGenerator smsVerifyGenerator() {
		return new SmsVerifyGenerator();
	}

	@Bean
	@ConditionalOnMissingBean(DefaultSmsSend.class)
	SmsSend smsSend() {
		return new DefaultSmsSend();
	}
}