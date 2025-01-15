package com.wy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.wy.feign.RemoteUserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserServiceImpl extends RemoteServiceImpl implements RemoteUserService {

	@Autowired
	private RestTemplate restTemplate;

	@CircuitBreaker(name = "userService", fallbackMethod = "fallback")
	public String getUserDetails(String userId) {
		return restTemplate.getForObject("http://user-service/users/" + userId, String.class);
	}

	public String fallback(String userId, Throwable throwable) {
		return "Fallback response for user " + userId;
	}

	@Override
	public Object checkUnique(String username) {
		return null;
	}

	@Override
	public Object getByParams(String username, Integer age) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getByParams(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setHeader(String authorization, MultiValueMap<String, String> headers) {
		// TODO Auto-generated method stub
		return null;
	}
}