package com.wy.crl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.base.AbstractCrl;
import com.wy.model.Button;

@RestController
@RequestMapping("button")
public class ButtonCrl extends AbstractCrl<Button> {
}