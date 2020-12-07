package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.Role;
import com.wy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("role")
public class RoleCrl extends AbstractCrl<Role> {
    @Autowired
    private RoleService roleService;
}