package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.UserRoleMapper;
import com.wy.model.UserRole;
import com.wy.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends AbstractService<UserRole> implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;
}