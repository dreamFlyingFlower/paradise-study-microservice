package com.wy;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * @description JWT鉴权,结构通常是A.B.C,A是头部信息(header)B是用户非隐私数据(payload)C签名(signature).
 *              通常A和B都会加密,而C在是在A和B加密的基础上再次加密
 * @instruction JWT中的3个部分都不能少,只有payload的数据可以为null,但是payload必须是存在的
 * @instruction header:数据结构:{"alg":"加密算法名称,如HMAC,AES,RSA","type":"token类型,固定为JWT"}
 * @instruction payload:分为已注册信息(registeredclaims),公开数据(publicclaims),私有数据(privateclaims),
 *              其中公开数据和私有数据由程序员自行定义,而注册信息则是已经定义好的规范.
 *              注册信息中常用的有iss(发行者),exp(到期时间),sub(主题),aud(受众)等,公开和私有数据不能和注册信息重复
 * @instruction JWT加密的方式:用header中指定的加密方式对header,payload分别进行加密,将加密后的数据用.连接,
 *              再用signature对已经加密和header和payload进行2次加密,然后用.连接到上一次加密的结果上.
 *              如:header加密.payload加密.signature(header加密.payload加密)加密
 * @author ParadiseWy
 * @date 2019年5月4日 上午11:12:06
 * @git {@link https://github.com/mygodness100}
 */
public class S_Jwt {

	/**
	 * jwt生成token
	 * @param header 请求头从的参数,必须有header,payload
	 * @return
	 */
	public static final String genericToken(final Map<String, String> header) {
		// 选择签名算法.参数为签名的密钥
		Algorithm hmac256 = Algorithm.HMAC256("secret key");
		Builder builder = JWT.create()
				// 签名时间
				.withIssuedAt(new Date())
				// 签名过期时间
				.withExpiresAt(DateUtils.addHours(new Date(), 1))
				// 签名者
				.withIssuer("呵呵,闲人")
				// 签名主题
				.withSubject("这就是个签名,你想看什么呢");
		// 增加payload中的信息
		header.forEach((k, u) -> builder.withClaim(k, u));
		// 返回签名的值
		return builder.sign(hmac256);
	}

	/**
	 * 验证token的合法性,若抛异常则验证失败
	 * @param cryptoStr 加密的字符串
	 * @return 是否合法
	 */
	public static final boolean verifyToken(String cryptoStr) {
		try {

			// 选择签名算法.参数为签名的密钥
			Algorithm hmac256 = Algorithm.HMAC256("secret key");
			// 指定算法,指定发布者
			JWTVerifier build = JWT.require(hmac256).withIssuer("呵呵,闲人").build();
			// 验证
			build.verify(cryptoStr);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}