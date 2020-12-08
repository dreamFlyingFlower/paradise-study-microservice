package com.wy.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import feign.Response;

@FeignClient("cloudclient1")
public interface UpdownService {

	/**
	 * 通过流直接下载文件,流并不形成文件,到前端之后再形成文件,数据直接从流中取,不可传递request对象
	 * 
	 * 客户端形成的数据只需要放置到流中即可,feign再次进行处理即可
	 */
	@PostMapping("updown/exportForm")
	Response exportForm(@RequestBody Object params);

	@RequestMapping("updown/downloadFile")
	public ResponseEntity<byte[]> downloadFile(@RequestParam(value = "fileType") String fileType);

	@PostMapping(value = "updown/uploadFile", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadFile(@RequestPart(value = "file") MultipartFile file,
			@RequestParam(value = "fileType") String fileType);
}