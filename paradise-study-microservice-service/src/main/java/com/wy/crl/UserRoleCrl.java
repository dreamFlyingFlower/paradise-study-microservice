package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.UserRole;
import com.wy.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("userRole")
public class UserRoleCrl extends AbstractCrl<UserRole> {
    @Autowired
    private UserRoleService userRoleService;
}