package com.wy.plugins;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;
import org.springframework.util.StringUtils;

/**
 * 自定义生成service和impl,此处不生成impl
 * 
 * @author paradiseWy 2018年8月30日
 */
public class ServicePlugin extends PluginAdapter {

	/** service基础接口 */
	private String baseService;

	/** service基础实现类 */
	private String baseServiceImpl;

	/** 是否生成service接口,默认生成 */
	private boolean validService;

	/** 是否生成service实现类,默认生成 */
	private boolean validServiceImpl;

	/** 工程所在目录 */
	private String targetProject;

	/** 工程所在包 */
	private String targetPackage;

	/** mapper.xml文件包 */
	private String targetPackageMapper;

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		baseService = this.properties.getProperty("baseService");
		baseServiceImpl = this.properties.getProperty("baseServiceImpl");
		targetPackageMapper = this.properties.getProperty("targetPackageMapper");
		validService = StringUtils.hasText(this.properties.getProperty("validService")) ? true
				: Boolean.parseBoolean(this.properties.getProperty("validService"));
		validServiceImpl = StringUtils.hasText(this.properties.getProperty("validServiceImpl")) ? true
				: Boolean.parseBoolean(this.properties.getProperty("validServiceImpl"));
		String targetProject = this.properties.getProperty("targetProject");
		if (StringUtility.stringHasValue(targetProject)) {
			this.targetProject = targetProject;
		} else {
			throw new RuntimeException("targetProject 属性不能为空！");
		}

		String targetPackage = this.properties.getProperty("targetPackage");
		if (StringUtility.stringHasValue(targetPackage)) {
			this.targetPackage = targetPackage;
		} else {
			throw new RuntimeException("targetPackage 属性不能为空！");
		}
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable table) {
		List<GeneratedJavaFile> result = new ArrayList<>();
		if (validService) {
			result.add(generateService(table));
		}
		if (validServiceImpl) {
			GeneratedJavaFile generatorServiceImpl = generatorServiceImpl(table);
			result.add(generatorServiceImpl);
		}
		return result;
	}

	/**
	 * 添加service接口
	 * 
	 * @param table 表数据类
	 * @return 接口
	 */
	private GeneratedJavaFile generateService(IntrospectedTable table) {
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(table.getBaseRecordType());
		String domainObjectName = table.getFullyQualifiedTable().getDomainObjectName();
		String service = MessageFormat.format("{0}.{1}Service", targetPackage, domainObjectName);
		Interface clazz = new Interface(new FullyQualifiedJavaType(service));
		clazz.setVisibility(JavaVisibility.PUBLIC);
		if (StringUtility.stringHasValue(baseService)) {
			clazz.addImportedType(entityType);
			clazz.addImportedType(new FullyQualifiedJavaType(baseService));
			clazz.addSuperInterface(new FullyQualifiedJavaType(baseService + "<" + entityType.getShortName() + ">"));
		}
		return new GeneratedJavaFile(clazz, targetProject, new DefaultJavaFormatter());
	}

	/**
	 * 生成service实现类
	 * 
	 * @param table 表数据
	 * @return 实现类
	 */
	private GeneratedJavaFile generatorServiceImpl(IntrospectedTable table) {
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(table.getBaseRecordType());
		String domainObjectName = table.getFullyQualifiedTable().getDomainObjectName();
		String service = MessageFormat.format("{0}.impl.{1}ServiceImpl", targetPackage, domainObjectName);
		TopLevelClass clazz = new TopLevelClass(new FullyQualifiedJavaType(service));
		clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
		clazz.addAnnotation("@Service");
		clazz.setVisibility(JavaVisibility.PUBLIC);
		// 实现接口
		clazz.addSuperInterface(new FullyQualifiedJavaType(MessageFormat.format("{0}Service", domainObjectName)));
		clazz.addImportedType(
				new FullyQualifiedJavaType(MessageFormat.format("{0}.{1}Service", targetPackage, domainObjectName)));
		// 继承基类service
		clazz.setSuperClass(new FullyQualifiedJavaType(MessageFormat.format("{0}.AbstractService", targetPackage)));
		if (StringUtility.stringHasValue(baseServiceImpl)) {
			clazz.addImportedType(entityType);
			clazz.addImportedType(new FullyQualifiedJavaType(baseServiceImpl));
			clazz.setSuperClass(new FullyQualifiedJavaType(baseServiceImpl + "<" + entityType.getShortName() + ">"));
		}
		// 添加mapper
		if (StringUtility.stringHasValue(targetPackageMapper)) {
			FullyQualifiedJavaType mapper = new FullyQualifiedJavaType(
					MessageFormat.format("{0}.{1}Mapper", targetPackageMapper, domainObjectName));
			Field field = new Field(MessageFormat.format("{0}Mapper", domainObjectName), mapper);
			clazz.addImportedType(mapper);
			clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
			field.addAnnotation("@Autowired");
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(mapper);
			field.setName(Character.toLowerCase(domainObjectName.charAt(0)) + domainObjectName.substring(1) + "Mapper");
			clazz.addField(field);
		}
		return new GeneratedJavaFile(clazz, targetProject, new DefaultJavaFormatter());
	}
}