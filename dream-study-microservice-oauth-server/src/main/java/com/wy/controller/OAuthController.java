//package com.wy.crl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.nimbusds.jose.jwk.JWKSet;
//
///**
// * 
// * 
// * @auther 飞花梦影
// * @date 2021-07-04 11:48:11
// * @git {@link https://github.com/dreamFlyingFlower}
// */
//@RequestMapping("oauth")
//@RestController
//public class OAuthCrl {
//
//	@Autowired
//	private JWKSet jwkSet;
//
//	@GetMapping(value = "token_key", produces = "application/json; charset=UTF-8")
//	public String keys() {
//		return this.jwkSet.toString();
//	}
//}