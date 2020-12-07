package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.Dict;
import com.wy.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dict")
public class DictCrl extends AbstractCrl<Dict> {
    @Autowired
    private DictService dictService;
}