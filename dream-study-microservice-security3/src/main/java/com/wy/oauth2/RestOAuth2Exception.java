//package com.wy.oauth2;
//
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
//
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//
///**
// * OAuth2自定义异常处理
// * 
// * @author 飞花梦影
// * @date 2022-09-13 16:42:16
// * @git {@link https://github.com/dreamFlyingFlower }
// */
//@JsonSerialize(using = RestOAuth2ExceptionSerializer.class)
//public class RestOAuth2Exception extends OAuth2Exception {
//
//	private static final long serialVersionUID = 7011148757571822899L;
//
//	public RestOAuth2Exception(String msg, Throwable t) {
//		super(msg, t);
//	}
//
//	public RestOAuth2Exception(String msg) {
//		super(msg);
//	}
//}