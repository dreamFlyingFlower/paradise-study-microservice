package com.wy.plugins;

import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;
import org.springframework.util.StringUtils;

/**
 * 自定义根据数据库表生成实体类
 * 
 * @author paradiseWy 2018年8月30日
 */
public class ModelPlugin extends PluginAdapter {

	/** 数据库名 */
	private String schema;

	/** 是否使用javax.persistence持久化注解 */
	private boolean usePersistence;

	/** lombok插件 */
	private LombokType lombok = LombokType.NONE;

	/** 基础实体类 */
	private String baseModel;

	/** 是否在父类上添加泛型,默认不添加 */
	private boolean genericType = false;

	/** 是否生成model,默认生成 */
	private boolean validModel;

	/** 是否生成modelexample,默认生成 */
	private boolean validModelExample;

	enum LombokType {
		NONE,
		SIMPLE,
		DATA,
		ACCESSORS,
		BUILDER
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		schema = context.getProperty("schema");
		usePersistence = StringUtility.isTrue(this.properties.getProperty("usePersistence"));
		lombok = LombokType.valueOf(this.properties.getProperty("lombok", "none"));
		baseModel = this.properties.getProperty("baseModel");
		genericType = Boolean.parseBoolean(this.properties.getProperty("genericType"));
		validModel = StringUtils.hasText(this.properties.getProperty("validModel")) ? true
				: Boolean.parseBoolean(this.properties.getProperty("validModel"));
		validModelExample = StringUtils.hasText(this.properties.getProperty("validModelExample")) ? true
				: Boolean.parseBoolean(this.properties.getProperty("validModelExample"));
		context.addProperty("baseModel", this.baseModel);
	}

	private String getDelimiterName(String name) {
		StringBuilder nameBuilder = new StringBuilder();
		if (StringUtility.stringHasValue(schema)) {
			nameBuilder.append(schema).append(".");
		}
		return nameBuilder.append(context.getBeginningDelimiter()).append(name).append(context.getEndingDelimiter())
				.toString();
	}

	/**
	 * 生成实体类
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		if (!validModel) {
			return false;
		}
		// 处理lombok注解
		handlerLombok(topLevelClass);
		// 获得表名
		String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
		// 如果有baseModel,继承baseModel
		if (StringUtility.stringHasValue(baseModel)) {
			topLevelClass.addImportedType(baseModel);
			/** 是否使用jpa持久化 */
			if (usePersistence) {
				topLevelClass.addImportedType("javax.persistence.Table");
				topLevelClass.addAnnotation(new StringBuilder("@Table(name = \"").append(getDelimiterName(tableName))
						.append("\")").toString());
			}
			/** 是否有泛型 */
			if (genericType) {
				topLevelClass.setSuperClass(
						baseModel + "<" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">");
			} else {
				topLevelClass.setSuperClass(baseModel);
			}
		}
		return true;
	}

	/**
	 * 生成实体类的字段
	 */
	@Override
	public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		if (!validModel) {
			return false;
		}
		/** 若不使用持久化组件,直接返回true */
		if (!usePersistence) {
			return true;
		}
		// 判断表中的主键,多主键
		for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
			// FIXME
			/** 需要将通用字段从实体类中剔除,但是不能从mapper.xml中剔除,要多一个list的参数,所有通用类放里面,每个字段校验 */
			if (introspectedColumn == column) {
				if (introspectedColumn.isStringColumn()) {
					// 字符串类型
				} else {
					// 数字类型
					topLevelClass.addImportedType("javax.persistence.Id");
					field.addAnnotation("@Id");
				}
				break;
			}
		}
		// 添加@Column注解,若数据库字段和java字段不一样,请单独修改
		topLevelClass.addImportedType("javax.persistence.Column");
		if (!introspectedColumn.isNullable()) {
			field.addAnnotation("@Column(nullable = false)");
		} else {
			field.addAnnotation("@Column");
		}
		// 若从数据库发现主键自增
		if (introspectedColumn.isIdentity()) {
			if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
				topLevelClass.addImportedType("javax.persistence.GeneratedValue");
				field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
			} else {
				topLevelClass.addImportedType("javax.persistence.GeneratedValue");
				topLevelClass.addImportedType("javax.persistence.GenerationType");
				field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
			}
		} else if (introspectedColumn.isSequenceColumn()) {
			// 在 Oracle 中,如果需要是 SEQ_TABLENAME,那么可以配置为 select SEQ_{1}
			String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
			String sql = MessageFormat.format(
					introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement(), tableName,
					tableName.toUpperCase());
			topLevelClass.addImportedType("javax.persistence.GeneratedValue");
			topLevelClass.addImportedType("javax.persistence.GenerationType");
			field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY, generator = \"" + sql + "\")");
		}
		return true;
	}

	/**
	 * 处理lombok注解
	 * 
	 * @param topLevelClass 需要生成的实体类
	 */
	private void handlerLombok(TopLevelClass topLevelClass) {
		switch (lombok) {
		case NONE:
			break;
		case SIMPLE:
			topLevelClass.addImportedType("lombok.Getter");
			topLevelClass.addImportedType("lombok.Setter");
			topLevelClass.addImportedType("lombok.NoArgsConstructor");
			topLevelClass.addAnnotation("@Getter");
			topLevelClass.addAnnotation("@Setter");
			topLevelClass.addAnnotation("@NoArgsConstructor");
			break;
		case DATA:
			topLevelClass.addImportedType("lombok.Data");
			topLevelClass.addImportedType("lombok.ToString;");
			topLevelClass.addAnnotation("@Data");
			topLevelClass.addAnnotation("@ToString");
			break;
		case ACCESSORS:
			topLevelClass.addImportedType("lombok.Getter");
			topLevelClass.addImportedType("lombok.Setter");
			topLevelClass.addImportedType("lombok.NoArgsConstructor");
			topLevelClass.addImportedType("lombok.experimental.Accessors");
			topLevelClass.addAnnotation("@Getter");
			topLevelClass.addAnnotation("@Setter");
			topLevelClass.addAnnotation("@NoArgsConstructor");
			topLevelClass.addAnnotation("@Accessors(fluent = true)");
			break;
		case BUILDER:
			topLevelClass.addImportedType("lombok.AllArgsConstructor");
			topLevelClass.addImportedType("lombok.Builder");
			topLevelClass.addImportedType("lombok.Getter");
			topLevelClass.addImportedType("lombok.NoArgsConstructor");
			topLevelClass.addImportedType("lombok.Setter");
			topLevelClass.addImportedType("lombok.ToString");
			topLevelClass.addAnnotation("@Getter");
			topLevelClass.addAnnotation("@Setter");
			topLevelClass.addAnnotation("@ToString");
			topLevelClass.addAnnotation("@NoArgsConstructor");
			topLevelClass.addAnnotation("@AllArgsConstructor");
			topLevelClass.addAnnotation("@Builder");
			break;
		default:
			break;
		}
	}

	/**
	 * 是否生成实体类的Example类
	 */
	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return validModelExample;
	}
}