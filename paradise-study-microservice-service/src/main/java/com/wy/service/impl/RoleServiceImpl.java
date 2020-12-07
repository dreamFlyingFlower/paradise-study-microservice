package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.RoleMapper;
import com.wy.model.Role;
import com.wy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends AbstractService<Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
}