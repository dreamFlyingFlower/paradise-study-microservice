package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.Menu;
import com.wy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("menu")
public class MenuCrl extends AbstractCrl<Menu> {
    @Autowired
    private MenuService menuService;
}