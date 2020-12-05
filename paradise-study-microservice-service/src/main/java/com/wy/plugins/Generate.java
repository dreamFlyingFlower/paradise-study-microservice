package com.wy.plugins;

import org.mybatis.generator.api.ShellRunner;

/**
 * 启动MyBatis生成各种实体类以及相应文件
 * 
 * @author ParadiseWY
 * @date 2020-12-02 10:10:05
 * @git {@link https://github.com/mygodness100}
 */
public class Generate {

	public static void main(String[] args) {
		args = new String[] { "-configfile", "src\\main\\resources\\generator\\generatorConfig.xml", "-overwrite" };
		ShellRunner.main(args);
	}
}