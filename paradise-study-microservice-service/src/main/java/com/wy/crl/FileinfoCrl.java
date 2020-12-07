package com.wy.crl;

import com.wy.base.AbstractCrl;
import com.wy.model.Fileinfo;
import com.wy.service.FileinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("fileinfo")
public class FileinfoCrl extends AbstractCrl<Fileinfo> {
    @Autowired
    private FileinfoService fileinfoService;
}