package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.MenuMapper;
import com.wy.model.Menu;
import com.wy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends AbstractService<Menu> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
}