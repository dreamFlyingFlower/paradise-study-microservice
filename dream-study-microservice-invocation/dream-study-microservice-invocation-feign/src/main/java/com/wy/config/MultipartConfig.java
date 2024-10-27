package com.wy.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class MultipartConfig {
	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	@Bean
	@Primary
	@Scope("prototype")
	public Encoder feignEncoder() {
		return new SpringFormEncoder(new SpringEncoder(messageConverters));
	}

	@Bean
	public feign.Logger.Level multipartLoggerLevel() {
		return feign.Logger.Level.FULL;
	}
}