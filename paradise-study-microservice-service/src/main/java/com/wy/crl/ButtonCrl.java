package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.Button;
import com.wy.service.ButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("button")
public class ButtonCrl extends AbstractCrl<Button> {
    @Autowired
    private ButtonService buttonService;
}