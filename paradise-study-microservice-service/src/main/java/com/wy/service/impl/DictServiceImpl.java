package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.DictMapper;
import com.wy.model.Dict;
import com.wy.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl extends AbstractService<Dict> implements DictService {
    @Autowired
    private DictMapper dictMapper;
}