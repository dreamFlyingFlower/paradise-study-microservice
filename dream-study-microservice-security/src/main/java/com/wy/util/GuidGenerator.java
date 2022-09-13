package com.wy.util;

import java.util.UUID;

import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

public abstract class GuidGenerator {

	private static RandomValueStringGenerator defaultClientSecretGenerator = new RandomValueStringGenerator(32);

	private GuidGenerator() {}

	public static String generate() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String generateClientSecret() {
		return defaultClientSecretGenerator.generate();
	}
}