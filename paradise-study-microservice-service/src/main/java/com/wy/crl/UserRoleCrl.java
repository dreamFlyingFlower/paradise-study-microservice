package com.wy.crl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.base.AbstractCrl;
import com.wy.model.UserRole;

@RestController
@RequestMapping("userRole")
public class UserRoleCrl extends AbstractCrl<UserRole> {
}