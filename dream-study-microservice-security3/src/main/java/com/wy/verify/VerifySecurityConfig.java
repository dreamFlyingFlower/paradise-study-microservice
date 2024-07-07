package com.wy.verify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.wy.filters.VerifyFilter;

@Configuration
public class VerifySecurityConfig
		extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired
	private VerifyFilter verifyFilter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(verifyFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}
}