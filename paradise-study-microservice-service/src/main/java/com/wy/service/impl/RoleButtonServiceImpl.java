package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.RoleButtonMapper;
import com.wy.model.RoleButton;
import com.wy.service.RoleButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleButtonServiceImpl extends AbstractService<RoleButton> implements RoleButtonService {
    @Autowired
    private RoleButtonMapper roleButtonMapper;
}