package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.RoleMenuMapper;
import com.wy.model.RoleMenu;
import com.wy.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuServiceImpl extends AbstractService<RoleMenu> implements RoleMenuService {
    @Autowired
    private RoleMenuMapper roleMenuMapper;
}