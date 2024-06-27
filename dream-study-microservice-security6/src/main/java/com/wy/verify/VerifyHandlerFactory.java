package com.wy.verify;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dream.flying.flower.result.ResultException;

@Component
public class VerifyHandlerFactory {

	@Autowired
	private Map<String, VerifyHandler> verifyHandlers;

	@Autowired
	private Map<String, VerifyInfo> verifyTypes;

	public VerifyInfo getVerifyInfo(String type) {
		for (Map.Entry<String, VerifyInfo> entry : verifyTypes.entrySet()) {
			if (Objects.equals(type.toLowerCase(),
					entry.getValue().getVerifyType().toLowerCase())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public VerifyHandler getHandler(String type) {
		return getHandler(getVerifyInfo(type));
	}

	public VerifyHandler getHandler(VerifyInfo verifyType) {
		if (verifyType == null) {
			throw new ResultException("验证方式不存在");
		}
		VerifyHandler verifyHandler = verifyHandlers.get(verifyType.getHandlerName());
		if (verifyHandler == null) {
			throw new ResultException("验证码处理器" + verifyType.getVerifyType() + "不存在");
		}
		return verifyHandler;
	}
}