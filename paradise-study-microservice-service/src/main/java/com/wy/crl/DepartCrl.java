package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.Depart;
import com.wy.service.DepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("depart")
public class DepartCrl extends AbstractCrl<Depart> {
    @Autowired
    private DepartService departService;
}