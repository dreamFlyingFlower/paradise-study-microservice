package com.wy.config;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.wy.constant.ConstAuthorizationServerRedis;

import dream.flying.flower.ConstDigest;
import dream.flying.flower.digest.RsaHelper;
import lombok.SneakyThrows;

/**
 * JWKSource存储到Redis或数据库
 * 
 * 将JWKSource保存至redis,每次重启从redis中获取,这样不管重不重启生成的都是同一个,也就不会出现服务重启后无法解析在有效期内的AccessToken问题了,但可能出现无法反序列化问题.
 * 要解决该问题,最主要的配置是来自JWKSet的实例,在JWKSet找到了toString()和对应的parse(),toString()将jwks的信息转为json字符串,parse读取并解析json字符串,将其转为JWKSet实例
 *
 * @author 飞花梦影
 * @date 2024-11-02 17:34:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class JwkSourceConfig {

	private StringRedisTemplate stringRedisTemplate;

	@Bean
	@SneakyThrows
	public JWKSource<SecurityContext> jwkSource() {
		// 先从redis获取
		String jwkSetCache =
				stringRedisTemplate.opsForValue().get(ConstAuthorizationServerRedis.AUTHORIZATION_JWS_PREFIX_KEY);
		if (ObjectUtils.isEmpty(jwkSetCache)) {
			KeyPair keyPair = RsaHelper.generateKeyPair(ConstDigest.KEY_SIZE_2048);
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey)
					// 若指定kid,则JWT的header里需要设置同样的kid,否则解析失败
					// .keyID(UUID.randomUUID().toString())
					.build();
			// 生成jws
			JWKSet jwkSet = new JWKSet(rsaKey);
			// 转为json字符串
			String jwkSetString = jwkSet.toString(Boolean.FALSE);
			// 存入redis
			stringRedisTemplate.opsForValue()
					.set(ConstAuthorizationServerRedis.AUTHORIZATION_JWS_PREFIX_KEY, jwkSetString);
			return new ImmutableJWKSet<>(jwkSet);
		}
		// 解析存储的jws
		JWKSet jwkSet = JWKSet.parse(jwkSetCache);
		return new ImmutableJWKSet<>(jwkSet);
	}
}