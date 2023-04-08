package com.wy.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wy.ConstDate;

/**
 * Jackson工具类
 *
 * @author 飞花梦影
 * @date 2022-11-10 23:27:40
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class JacksonHelper {

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(Date.class, new DateSerializer());
		javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
		javaTimeModule.addSerializer(new LocalDateSerializer(DateTimeFormatter.ofPattern(ConstDate.DATE)));
		javaTimeModule.addDeserializer(LocalDate.class,
		        new LocalDateDeserializer(DateTimeFormatter.ofPattern(ConstDate.DATE)));
		javaTimeModule.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(ConstDate.DATETIME)));
		javaTimeModule.addDeserializer(LocalDateTime.class,
		        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(ConstDate.DATETIME)));

		objectMapper.registerModule(javaTimeModule);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	public static ObjectMapper getInstance() {
		return objectMapper;
	}

	/**
	 * JSON将序列化字符串转换为Map<String, Object>
	 * 
	 * @param json json字符串
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> parseMap(Object json) {
		if (Objects.isNull(json)) {
			return Collections.emptyMap();
		}
		try {
			return objectMapper.readValue(
			        String.class == json.getClass() ? (String) json : objectMapper.writeValueAsString(json),
			        new TypeReference<Map<String, Object>>() {
			        });
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

	/**
	 * JSON将序列化字符串转换为Map<K,V>
	 * 
	 * @param <K> key泛型
	 * @param <V> value泛型
	 * @param json json字符串
	 * @param empty 一个空的Map<K,V>,为了确定转出的泛型正确
	 * @return Map<K, V>
	 */
	public static <K, V> Map<K, V> parseMap(Object json, Map<K, V> empty) {
		if (Objects.isNull(json)) {
			return Collections.emptyMap();
		}
		try {
			return objectMapper.readValue(
			        String.class == json.getClass() ? (String) json : objectMapper.writeValueAsString(json),
			        new TypeReference<Map<K, V>>() {
			        });
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
}