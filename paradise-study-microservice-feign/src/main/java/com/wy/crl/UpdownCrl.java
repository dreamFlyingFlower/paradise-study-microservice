package com.wy.crl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wy.service.UpdownService;
import com.wy.utils.ObjUtils;

import feign.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "专用上传下载API")
@RestController
@RequestMapping("updown")
public class UpdownCrl {

	@Autowired
	private UpdownService updownService;

	@ApiOperation("直接使用流进行文件的传输,前端形成文件")
	@GetMapping("exportForm")
	public ResponseEntity<byte[]> exportForm(HttpServletRequest request) {
		Response response = updownService.exportForm(ObjUtils.transReq(request));
		Response.Body body = response.body();
		try (InputStream inputStream = body.asInputStream()) {
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			HttpHeaders heads = new HttpHeaders();
			// 中文文件名需要传唤为iso8859
			heads.add(HttpHeaders.CONTENT_DISPOSITION, "attchament;filename="
					+ new String("excel表的名字.xls".getBytes("GBK"), "ISO8859-1"));
			// 若是直接用流输出excel,此处可不设置,设置了前端反而会出现警告
			// heads.add(HttpHeaders.CONTENT_TYPE,
			// MediaType.APPLICATION_JSON_VALUE);
			return new ResponseEntity<byte[]>(b, heads, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * FIXME 文件下载,未测试,只能下载比较小的文件,若是大文件最好是给url地址让别人下,否则占用线程
	 * @param fileName
	 */
	@RequestMapping("/user_downloadFile")
	public Object user_downloadFile(HttpServletRequest request, HttpServletResponse response,
			String fileName) {
		return updownService.downloadFile(fileName);
	}

	/**
	 * FIXME 文件上传,未测试
	 * @param fileName
	 */
	@PostMapping("/user_uploadFile")
	public Object user_uploadFile(HttpServletRequest request, HttpServletResponse response,
			@RequestPart(value = "file") MultipartFile file, String fileType) {
		return updownService.uploadFile(file, fileType);
	}
}