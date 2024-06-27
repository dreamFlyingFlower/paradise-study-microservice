package com.wy.oauth2;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * OAuth2认证服务异常序列化
 * 
 * @author 飞花梦影
 * @date 2022-09-13 16:39:54
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@SuppressWarnings("deprecation")
public class RestOAuth2ExceptionSerializer extends StdSerializer<RestOAuth2Exception> {

	private static final long serialVersionUID = 1374299379306174503L;

	protected RestOAuth2ExceptionSerializer() {
		super(RestOAuth2Exception.class);
	}

	@Override
	public void serialize(RestOAuth2Exception value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeObjectField("code", value.getHttpErrorCode());
		String errorMessage = value.getOAuth2ErrorCode();
		if (errorMessage != null) {
			errorMessage = HtmlUtils.htmlEscape(errorMessage);
		}
		gen.writeStringField("msg", value.getMessage());
		String summary = value.getSummary();
		gen.writeStringField("data", summary);

		if (value.getAdditionalInformation() != null) {
			for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
				String key = entry.getKey();
				String add = entry.getValue();
				gen.writeStringField(key, add);
			}
		}
		gen.writeEndObject();
	}
}