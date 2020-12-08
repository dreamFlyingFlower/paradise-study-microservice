package com.wy.crl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.base.AbstractCrl;
import com.wy.model.Depart;

@RestController
@RequestMapping("depart")
public class DepartCrl extends AbstractCrl<Depart> {
}