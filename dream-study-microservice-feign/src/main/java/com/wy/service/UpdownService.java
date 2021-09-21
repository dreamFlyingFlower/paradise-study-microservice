package com.wy.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import feign.Response;

public interface UpdownService {

	Response exportForm(Object params);

	ResponseEntity<byte[]> downloadFile(String fileType);

	String uploadFile(MultipartFile file, String fileType);
}