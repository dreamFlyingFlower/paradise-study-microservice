package com.wy.crl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.base.AbstractCrl;
import com.wy.model.RoleMenu;

@RestController
@RequestMapping("roleMenu")
public class RoleMenuCrl extends AbstractCrl<RoleMenu> {
}