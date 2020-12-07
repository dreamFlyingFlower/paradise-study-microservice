package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.UserMapper;
import com.wy.model.User;
import com.wy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractService<User> implements UserService {
    @Autowired
    private UserMapper userMapper;
}