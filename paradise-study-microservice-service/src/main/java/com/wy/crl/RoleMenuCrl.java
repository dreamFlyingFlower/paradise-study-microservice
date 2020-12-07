package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.RoleMenu;
import com.wy.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("roleMenu")
public class RoleMenuCrl extends AbstractCrl<RoleMenu> {
    @Autowired
    private RoleMenuService roleMenuService;
}