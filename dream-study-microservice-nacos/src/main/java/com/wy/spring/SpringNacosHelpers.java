package com.wy.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Nacos工具类.配置dream-study-java-spring/com/wy/classloader使用,使用nacos动态加载
 *
 * @author 飞花梦影
 * @date 2024-05-09 09:21:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@Slf4j
public class SpringNacosHelpers {

	private String dataId = "dream-loadjars.yml";

	@Autowired
	private NacosConfig nacosConfig;

	@Value("${spring.cloud.nacos.config.group}")
	private String group;

	/**
	 * 从nacos配置文件中,添加初始化配置
	 * 
	 * @param jarName 要移除的jar包名
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void addJarName(String jarName) throws Exception {
		ConfigService configService = nacosConfig.configService();
		String content = configService.getConfig(dataId, group, 5000);
		// 修改配置文件内容
		YAMLMapper yamlMapper = new YAMLMapper();
		ObjectMapper jsonMapper = new ObjectMapper();
		Object yamlObject = yamlMapper.readValue(content, Object.class);

		String jsonString = jsonMapper.writeValueAsString(yamlObject);
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		List<String> loadjars;
		if (jsonObject.containsKey("loadjars")) {
			loadjars = (List<String>) jsonObject.get("loadjars");
		} else {
			loadjars = new ArrayList<>();
		}
		if (!loadjars.contains(jarName)) {
			loadjars.add(jarName);
		}
		jsonObject.put("loadjars", loadjars);

		Object yaml = yamlMapper.readValue(jsonMapper.writeValueAsString(jsonObject), Object.class);
		String newYamlString = yamlMapper.writeValueAsString(yaml);
		boolean b = configService.publishConfig(dataId, group, newYamlString);

		if (b) {
			log.info("nacos配置更新成功");
		} else {
			log.info("nacos配置更新失败");
		}
	}
}