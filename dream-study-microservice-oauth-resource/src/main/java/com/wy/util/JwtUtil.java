package com.wy.util;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * JWT加密
 *
 * @author 飞花梦影
 * @date 2021-07-02 17:37:44
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class JwtUtil {

	private static final String KEY_STORE_FILE = "keystore-oauth2-demo";

	private static final String KEY_STORE_PASSWORD = "admin1234";

	private static final String KEY_ALIAS = "oauth2-demo-key";

	private static KeyStoreKeyFactory KEY_STORE_KEY_FACTORY =
			new KeyStoreKeyFactory(new ClassPathResource(KEY_STORE_FILE), KEY_STORE_PASSWORD.toCharArray());

	public static final String VERIFIER_KEY_ID =
			new String(Base64.getEncoder().encode(KeyGenerators.secureRandom(32).generateKey()));

	public static RSAPublicKey getVerifierKey() {
		return (RSAPublicKey) getKeyPair().getPublic();
	}

	public static RSAPrivateKey getSignerKey() {
		return (RSAPrivateKey) getKeyPair().getPrivate();
	}

	private static KeyPair getKeyPair() {
		return KEY_STORE_KEY_FACTORY.getKeyPair(KEY_ALIAS);
	}
}