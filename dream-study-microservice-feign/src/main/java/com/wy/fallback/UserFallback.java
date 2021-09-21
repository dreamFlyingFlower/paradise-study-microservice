package com.wy.fallback;

import org.springframework.stereotype.Component;

import com.wy.service.impl.UserServiceImpl;

@Component
public class UserFallback extends FeignFallback<UserServiceImpl> {

}