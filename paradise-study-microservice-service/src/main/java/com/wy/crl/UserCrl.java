package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.User;
import com.wy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserCrl extends AbstractCrl<User> {
    @Autowired
    private UserService userService;
}