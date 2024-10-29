package com.wy.jwt;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import dream.flying.flower.helper.DateHelper;

/**
 * JWT鉴权,由3部分构成:头部信息(header).用户非隐私数据(payload).签名(signature),3部分中间用点(.)分割
 * 通常header和payload都会重新编码或加密,而signature在是在header和payload编码或加密的基础上再次加密
 * 
 * JWT中的3个部分都不能少,只有payload的数据可以为null,但是payload必须是存在的:
 * 
 * <pre>
 *  header:数据结构:{"alg":"加密算法名称,如HMAC,AES,RSA","type":"token类型,固定为JWT"}
 *  payload:包含已注册信息(registeredclaims),公开数据(publicclaims),私有数据(privateclaims),
 *			其中公开数据和私有数据由程序员自行定义,而注册信息则是已经定义好的规范.
 *         注册信息中常用的有iss(发行者),exp(到期时间),sub(主题),aud(受众)等,公开和私有数据不能和注册信息重复
 *	signature:签名,非对称加密,防篡改.这个部分需要公私钥进行加解密.发送方用私钥加密,接收方用公钥解密
 *  
 *	签名算法:用base64分别对header,payload进行编码,将编码后的数据用.连接得到temp,
 *			再用header中指定的加密算法,利用secret(私钥)对temp进行加密,将得到的结果用.连接到temp上,得到最终的结果
 *     		如:header编码.payload编码.加密算法(header编码.payload编码,私钥)
 * </pre>
 *
 * @author 飞花梦影
 * @date 2021-04-10 16:07:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class S_Jwt {

	/**
	 * jwt生成token
	 * 
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
				.withExpiresAt(DateHelper.dateAdd(new Date(), Calendar.HOUR_OF_DAY, 1))
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
	 * 
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