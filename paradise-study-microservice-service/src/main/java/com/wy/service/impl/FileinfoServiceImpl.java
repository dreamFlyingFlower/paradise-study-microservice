package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.FileinfoMapper;
import com.wy.model.Fileinfo;
import com.wy.service.FileinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileinfoServiceImpl extends AbstractService<Fileinfo> implements FileinfoService {
    @Autowired
    private FileinfoMapper fileinfoMapper;
}