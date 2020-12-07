package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.DepartMapper;
import com.wy.model.Depart;
import com.wy.service.DepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartServiceImpl extends AbstractService<Depart> implements DepartService {
    @Autowired
    private DepartMapper departMapper;
}