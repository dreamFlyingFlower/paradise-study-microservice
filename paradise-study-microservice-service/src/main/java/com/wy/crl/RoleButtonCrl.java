package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.RoleButton;
import com.wy.service.RoleButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("roleButton")
public class RoleButtonCrl extends AbstractCrl<RoleButton> {
    @Autowired
    private RoleButtonService roleButtonService;
}