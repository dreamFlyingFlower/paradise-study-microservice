package com.wy.kafka.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.wy.kafka.wechat.conf.WechatTemplateProperties;

public interface WechatTemplateService {

	/**
	 * 获取微信调查问卷模板 - 获取目前active为true的模板就可以了
	 * 
	 * @return WechatTemplate
	 */
	WechatTemplateProperties.WechatTemplate getWechatTemplate();

	/**
	 * 上报调查问卷填写结果
	 * 
	 * @param reportInfo 结果JSON
	 */
	void templateReported(JSONObject reportInfo);

	/**
	 * 获取调查问卷的统计结果
	 * 
	 * @param templateId 模板ID
	 * @return 统计结果
	 */
	JSONObject templateStatistics(String templateId);
}