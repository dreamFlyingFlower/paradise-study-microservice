package com.wy.crl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wy.base.AbstractCrl;
import com.wy.model.Fileinfo;

@RestController
@RequestMapping("fileinfo")
public class FileinfoCrl extends AbstractCrl<Fileinfo> {
}