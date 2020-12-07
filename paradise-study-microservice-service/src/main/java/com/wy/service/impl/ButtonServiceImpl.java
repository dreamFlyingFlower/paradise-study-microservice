package com.wy.service.impl;

import com.wy.base.AbstractService;
import com.wy.mapper.ButtonMapper;
import com.wy.model.Button;
import com.wy.service.ButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ButtonServiceImpl extends AbstractService<Button> implements ButtonService {
    @Autowired
    private ButtonMapper buttonMapper;
}