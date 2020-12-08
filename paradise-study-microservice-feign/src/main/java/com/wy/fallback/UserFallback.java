package com.wy.fallback;

import org.springframework.stereotype.Component;

import com.wy.service.impl.UserImpl;

@Component
public class UserFallback extends FeignFallback<UserImpl> {

}