package com.wy.plugins;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 生成dao层方法
 * 
 * @author paradiseWy
 */
public class MapperPlugin extends PluginAdapter {

	/** 是否生成mapper接口,默认生成 */
	private Boolean validMapper;

	/** 是否生成mapper对应的xml文件,默认生成 */
	private Boolean validMapperXml;

	/** 通用Mapper接口 */
	private Set<String> baseMappers = new HashSet<>();

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		validMapper = StringUtils.hasText(this.properties.getProperty("validMapper")) ? true
				: Boolean.parseBoolean(this.properties.getProperty("validMapper"));
		validMapperXml = StringUtils.hasText(this.properties.getProperty("validMapperXml")) ? true
				: Boolean.parseBoolean(this.properties.getProperty("validMapperXml"));
		String baseMappers = this.properties.getProperty("baseMappers");
		if (StringUtility.stringHasValue(baseMappers)) {
			Collections.addAll(this.baseMappers, baseMappers.split(","));
		}
	}

	/**
	 * 自定义生成的base_column_list
	 */
	@Override
	public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		return super.sqlMapBaseColumnListElementGenerated(element, introspectedTable);
	}

	/**
	 * 生成Mapper中的xml文件,返回false不生成
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		// 生成批量新增inserts
		XmlElement inserts = generateInserts(introspectedTable);
		if (Objects.nonNull(inserts)) {
			document.getRootElement().addElement(inserts);
		}
		// 生成主键批量删除deleteByPrimaryKeys
		XmlElement deleteByPrimaryKeys = generateDeleteByPrimaryKeys(introspectedTable);
		if (Objects.nonNull(deleteByPrimaryKeys)) {
			document.getRootElement().addElement(deleteByPrimaryKeys);
		}
		// 生成清空表数据deleteAll
		XmlElement deleteAll = generateDeleteAll(introspectedTable);
		if (Objects.nonNull(deleteAll)) {
			document.getRootElement().addElement(deleteAll);
		}
		// 生成根据实体类非null字段分页查询selectEntitys
		XmlElement selectEntitys = generateSelectEntitys(introspectedTable);
		if (Objects.nonNull(selectEntitys)) {
			document.getRootElement().addElement(selectEntitys);
		}
		// 生成根据任意非null字段分页查询selectLists
		XmlElement selectLists = generateSelectLists(introspectedTable);
		if (Objects.nonNull(selectLists)) {
			document.getRootElement().addElement(selectLists);
		}
		// 生成根据实体类非null参数为条件计数countByEntity
		XmlElement countByEntity = generateCountByEntity(introspectedTable);
		if (Objects.nonNull(countByEntity)) {
			document.getRootElement().addElement(countByEntity);
		}
		// 生成获得实体类中的数字类型获得最大值getMaxValue
		XmlElement getMaxValue = generateGetMaxValue(introspectedTable);
		if (Objects.nonNull(getMaxValue)) {
			document.getRootElement().addElement(getMaxValue);
		}
		// 生成获得实体类中的时间类型最大值getMaxTime
		XmlElement getMaxTime = generateGetMaxTime(introspectedTable);
		if (Objects.nonNull(getMaxTime)) {
			document.getRootElement().addElement(getMaxTime);
		}
		return validMapperXml;
	}

	/**
	 * 根据集合参数批量插入数据,所有值都插入,即使是null
	 * 
	 * 若集合中某条数据的某个字段为null,而其他数据不为null,则插入字段将不一样,无法批量插入
	 * 
	 * 主键只能是自增长主键或字符串主键
	 * 
	 * @param introspectedTable 表信息
	 * @return 生成的xml信息
	 */
	private XmlElement generateInserts(IntrospectedTable introspectedTable) {
		XmlElement insertSelectives = new XmlElement("insert");
		insertSelectives.addAttribute(new Attribute("id", "inserts"));
		insertSelectives.addAttribute(new Attribute("parameterType", "list"));
		StringBuffer beginBuffer = new StringBuffer("insert into %s (%s) ");
		List<String> columnKeys = new ArrayList<String>();
		StringBuilder columnValues = new StringBuilder(
				" values <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">(");
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		// 主键是否为字符串,true是,false数字类型,但只管自增主键
		boolean primaryKeyStr = false;
		// 处理主键
		List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
		if (!CollectionUtils.isEmpty(primaryKeyColumns) && primaryKeyColumns.size() == 1) {
			int jdbcType = primaryKeyColumns.get(0).getJdbcType();
			if (Types.VARCHAR == jdbcType) {
				primaryKeyStr = true;
			}
		}
		// 是否为最后一个值
		boolean last = false;
		for (IntrospectedColumn column : columnList) {
			if (column == columnList.get(columnList.size() - 1)) {
				last = true;
			}
			columnKeys.add(column.getActualColumnName());
			if (StringUtils.hasText(column.getDefaultValue())) {
				columnValues.append("<choose><when test=\"item.").append(column.getJavaProperty()).append("!= null\">")
						.append(column.getActualColumnName()).append(" = #{item.").append(column.getJavaProperty())
						.append(",jdbcType=").append(column.getJdbcTypeName())
						.append(last ? "}</when><otherwise>" : "},</when><otherwise>").append(column.getDefaultValue())
						.append(last ? "</otherwise></choose>" : ",</otherwise></choose>");
			} else {
				if (column.isIdentity() && column.isAutoIncrement()) {
					columnValues.append(String.format(last ? "null" : "null,"));
				} else if (column.isIdentity() && primaryKeyStr) {
					columnValues.append(
							last ? "(select replace(uuid(),'-','') as id)" : "(select replace(uuid(),'-','') as id),");
				} else {
					columnValues.append(String.format(last ? "#{item.%s,jdbcType=%s}" : "#{item.%s,jdbcType=%s},",
							column.getJavaProperty(), column.getJdbcTypeName()));
				}
			}
		}
		columnValues.append(")</foreach>");
		String benginStr = String.format(beginBuffer.toString(),
				introspectedTable.getTableConfiguration().getTableName(), String.join(",", columnKeys));
		insertSelectives.addElement(new TextElement(benginStr + columnValues.toString()));
		return insertSelectives;
	}

	private XmlElement generateDeleteByPrimaryKeys(IntrospectedTable introspectedTable) {
		XmlElement deleteByPrimaryKeys = new XmlElement("delete");
		deleteByPrimaryKeys.addAttribute(new Attribute("id", "deleteByPrimaryKeys"));
		deleteByPrimaryKeys.addAttribute(new Attribute("parameterType", "list"));
		List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
		if (CollectionUtils.isEmpty(primaryKeyColumns) || primaryKeyColumns.size() > 1) {
			return null;
		}
		String sqlFormat = "delete from  %s where %s in "
				+ "<foreach open=\"(\" close=\")\" collection=\"list\" item=\"%s\" separator=\",\">"
				+ "#{%s,jdbcType=%s}</foreach>";
		StringBuffer sqlString = new StringBuffer(
				String.format(sqlFormat, introspectedTable.getTableConfiguration().getTableName(),
						primaryKeyColumns.get(0).getActualColumnName(), primaryKeyColumns.get(0).getJavaProperty(),
						primaryKeyColumns.get(0).getJavaProperty(), primaryKeyColumns.get(0).getActualTypeName()));
		deleteByPrimaryKeys.addElement(new TextElement(sqlString.toString()));
		return deleteByPrimaryKeys;
	}

	private XmlElement generateDeleteAll(IntrospectedTable introspectedTable) {
		XmlElement deleteAll = new XmlElement("delete");
		deleteAll.addAttribute(new Attribute("id", "deleteAll"));
		StringBuffer sqlString = new StringBuffer(
				String.format("delete from  %s ", introspectedTable.getTableConfiguration().getTableName()));
		deleteAll.addElement(new TextElement(sqlString.toString()));
		return deleteAll;
	}

	private XmlElement generateSelectEntitys(IntrospectedTable introspectedTable) {
		XmlElement listByEntity = new XmlElement("select");
		listByEntity.addAttribute(new Attribute("id", "selectEntitys"));
		listByEntity.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		StringBuilder whereBuilder = new StringBuilder();
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		for (IntrospectedColumn column : columnList) {
			whereBuilder.append(String.format("<if test = \"%s != null \"> and %s = #{%s,jdbcType=%s} </if>",
					column.getJavaProperty(), column.getActualColumnName(), column.getJavaProperty(),
					column.getJdbcTypeName()));
		}
		StringBuffer sqlString = new StringBuffer("select <include refid=\"Base_Column_List\" /> from ")
				.append(introspectedTable.getTableConfiguration().getTableName()).append("<where>").append(whereBuilder)
				.append("</where>");
		listByEntity.addElement(new TextElement(sqlString.toString()));
		return listByEntity;
	}

	private XmlElement generateSelectLists(IntrospectedTable introspectedTable) {
		XmlElement selectLists = new XmlElement("select");
		selectLists.addAttribute(new Attribute("id", "selectLists"));
		selectLists.addAttribute(new Attribute("resultType", "map"));
		StringBuilder whereBuilder = new StringBuilder();
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		for (IntrospectedColumn column : columnList) {
			whereBuilder.append(String.format("<if test = \"%s != null \"> and %s = #{%s,jdbcType=%s} </if>",
					column.getJavaProperty(), column.getActualColumnName(), column.getJavaProperty(),
					column.getJdbcTypeName()));
		}
		StringBuffer sqlString = new StringBuffer("select <include refid=\"Base_Column_List\" /> from ")
				.append(introspectedTable.getTableConfiguration().getTableName()).append("<where>").append(whereBuilder)
				.append("</where>");
		selectLists.addElement(new TextElement(sqlString.toString()));
		return selectLists;
	}

	private XmlElement generateCountByEntity(IntrospectedTable introspectedTable) {
		XmlElement countByEntity = new XmlElement("select");
		countByEntity.addAttribute(new Attribute("id", "countByEntity"));
		countByEntity.addAttribute(new Attribute("resultType", "java.lang.Long"));
		StringBuilder whereBuilder = new StringBuilder();
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		for (IntrospectedColumn column : columnList) {
			whereBuilder.append(String.format("<if test = \"%s != null \">and %s = #{%s,jdbcType=%s} </if>",
					column.getJavaProperty(), column.getActualColumnName(), column.getJavaProperty(),
					column.getJdbcTypeName()));
		}
		StringBuffer sqlString = new StringBuffer("select count(*) from ")
				.append(introspectedTable.getTableConfiguration().getTableName()).append("<where>").append(whereBuilder)
				.append("</where>");
		countByEntity.addElement(new TextElement(sqlString.toString()));
		return countByEntity;
	}

	private XmlElement generateGetMaxValue(IntrospectedTable introspectedTable) {
		XmlElement getMaxValue = new XmlElement("select");
		getMaxValue.addAttribute(new Attribute("id", "getMaxValue"));
		getMaxValue.addAttribute(new Attribute("resultType", "java.lang.Long"));
		StringBuilder whereBuilder = new StringBuilder();
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		try {
			for (IntrospectedColumn column : columnList) {
				Class<?> forName = Class.forName(column.getFullyQualifiedJavaType().getFullyQualifiedName());
				if (Number.class.isAssignableFrom(forName)) {
					whereBuilder.append(String.format("<when test=\"'%s' == _parameter.column\">max(%s)</when>",
							column.getJavaProperty(), column.getActualColumnName()));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sqlString = new StringBuffer("select <choose>").append(whereBuilder)
				.append("<otherwise>max(0)</otherwise>  </choose>  from ")
				.append(introspectedTable.getTableConfiguration().getTableName());
		getMaxValue.addElement(new TextElement(sqlString.toString()));
		return getMaxValue;
	}

	private XmlElement generateGetMaxTime(IntrospectedTable introspectedTable) {
		XmlElement getMaxTime = new XmlElement("select");
		getMaxTime.addAttribute(new Attribute("id", "getMaxTime"));
		getMaxTime.addAttribute(new Attribute("resultType", "java.util.Date"));
		StringBuilder whereBuilder = new StringBuilder();
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		try {
			for (IntrospectedColumn column : columnList) {
				Class<?> forName = Class.forName(column.getFullyQualifiedJavaType().getFullyQualifiedName());
				if (Date.class.isAssignableFrom(forName)) {
					whereBuilder.append(String.format("<when test=\"%s == ${column}\">max(%s)</when>",
							column.getJavaProperty(), column.getJavaProperty(), column.getActualColumnName()));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sqlString = new StringBuffer("select <choose>").append(whereBuilder)
				.append("<otherwise>1970-01-01 00:00:00</otherwise>  </choose>  from ")
				.append(introspectedTable.getTableConfiguration().getTableName());
		getMaxTime.addElement(new TextElement(sqlString.toString()));
		return getMaxTime;
	}

	/**
	 * 使用mybatis生成mapper接口,返回true的时候表示生成
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
		if (!validMapper) {
			return false;
		}
		if (this.baseMappers.size() == 0) {
			return true;
		}
		// 获取实体类
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		// import 实体类
		interfaze.addImportedType(entityType);
		// 添加 @Mapper 注解
		interfaze.addAnnotation("@Mapper");
		// import 接口
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
		for (String mapper : baseMappers) {
			interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
			interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
		}
		return true;
	}
}