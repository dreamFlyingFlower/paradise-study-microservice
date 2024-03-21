package com.wy.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;

/**
 * 全局配置以及不需要生成的方法
 * 
 * @description 1.validate:表示该文件是否被调用解析
 *              2.clientGenerated(Interface,TopLevelClass,IntrospectedTable):生成Mapper接口
 *              3.clientXXXMethodGenerated(Method,Interface,IntrospectedTable):生成Mapper接口中抽象方法
 *              4.clientXXXMethodGenerated(Method,TopLevelClass,IntrospectedTable):生成dao的实现类,主要针对ibatis
 *              5.sqlMapDocumentGenerated:生成Mapper对应的xml中的document,统一对xml进行处理
 *              6.sqlMapGenerated:调用完sqlMapDocumentGenerated之后调用该方法生成xml
 *              7.sqlMapXXXElementGenerated:生成Mapper对应的xml中的方法
 *              8.clientBasicXXXMethodGenerated:需要1.3.6之后的版本且runtime为MyBatis3DynamicSQL才生效,不兼容以前的sql,但更容易使用
 *              9.XXXBlobXXX:处理带有blob列的类
 *              10.XXXSelectAllMethodGenerated:只有当runtime为MyBatis3Simple时才生效
 *              11.providerXXX:当调用接口中的方法时调用
 * @instruction modelFieldGenerated:生成实体类中的字段
 *              modelGetterMethodGenerated:实体类中为字段生成getter方法
 *              modelSetterMethodGenerated:实体类中为字段生成setter方法
 *              modelExampleClassGenerated:生成XXXExample类,是example的条件类
 *              modelPrimaryKeyClassGenerated:当defaultModelType为hierarchical时,生成主键类
 *              modelBaseRecordClassGenerated:当defaultModelType为hierarchical时,生成简单字段类
 *              modelRecordWithBLOBsClassGenerated:当defaultModelType为hierarchical时,生成包含blob列的类
 * 
 *              clientCountByExampleMethodGenerated:生成countByExample抽象方法
 *              clientDeleteByExampleMethodGenerated:生成deleteByExample抽象方法
 *              clientDeleteByPrimaryKeyMethodGenerated:生成deleteByPrimaryKey抽象方法
 *              clientInsertMethodGenerated:生成insert抽象方法
 *              clientInsertSelectiveMethodGenerated:生成insertSelective抽象方法
 *              clientSelectByPrimaryKeyMethodGenerated:生成selectByPrimaryKey抽象方法
 *              clientUpdateByExampleSelectiveMethodGenerated:生成updateByExampleSelective抽象方法
 *              clientUpdateByPrimaryKeySelectiveMethodGenerated:生成updateByPrimaryKeySelective抽象方法
 * 
 *              sqlMapCountByExampleElementGenerated:xml中生成countByExample的select语句
 *              sqlMapDeleteByExampleElementGenerated:xml中生成deleteByExample的delete语句
 *              sqlMapDeleteByPrimaryKeyElementGenerated:xml中生成deleteByPrimaryKey的delete语句
 *              sqlMapInsertElementGenerated:xml中生成insert的insert语句
 *              sqlMapSelectByPrimaryKeyElementGenerated:xml中生成selectByPrimaryKey的select语句
 *              sqlMapUpdateByExampleSelectiveElementGenerated:xml中生成updateByExampleSelective的update语句
 *              sqlMapUpdateByPrimaryKeySelectiveElementGenerated:xml中生成updateByPrimaryKeySelective的update语句
 *              sqlMapInsertSelectiveElementGenerated:xml中生成insertSelective的insert语句
 *              sqlMapBaseColumnListElementGenerated:xml中生成Base_Column_List的sql语句
 *              sqlMapExampleWhereClauseElementGenerated:xml中生成Example_Where_Clause和Update_By_Example_Where_Clause的sql语句
 * 
 *              contextGenerateAdditionalJavaFiles:生成额外的java文件,mbg未实现
 *              contextGenerateAdditionalXmlFiles:生成额外的xml文件,mbd未实现
 * @attention 1.自定义的实体类,mapper,xml插件继承PluginAdapter
 *            2.CachePlugin:在xml中生成cache元素,需要MyBatis3/MyBatis3Simple
 *            3.CaseInsensitiveLikePlugin:在XXXExample类中生成大小写敏感的like方法
 *            4.EqualsHashCodePlugin:给java模版生成equals和hashcode方法
 *            5.FluentBuilderMethodsPlugin:生成fluent风格的实体类代码
 *            6.MapperConfigPlugin:生成一个默认的MapperConfig.xml文件骨架
 *            7.RenameExampleClassPlugin:可使用正则重命名XXXExample类
 *            8.RowBoundsPlugin:可生成一个新的selectByExample方法,这个方法可接受一个RowBounds参数,主要用来分页
 *            9.SerializablePlugin:实现实体类的序列化
 *            10.SqlMapConfigPlugin:生成一个SqlMapConfig文件,包含sqlMap条目所有生成的sql映射
 *            11.ToStringPlugin:为生成的java实体类创建一个toString方法
 *            12.VirtualPrimaryKeyPlugin:此插件可用于指定充当主键的列,即使它们没有严格定义为数据库中的主键
 * @author paradiseWy
 */
public class CommonPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	/**
	 * 注入生成器上下文
	 */
	@Override
	public void setContext(Context context) {
		super.setContext(context);
		// 设置默认的注释生成器
		CommentGeneratorConfiguration configuration = new CommentGeneratorConfiguration();
		// 设置自定义的注释生成器
		configuration.setConfigurationType(CommentPlugin.class.getCanonicalName());
		// 往注释插件中添加需要的属性
		configuration.addProperty("suppressDate", this.context.getProperty("suppressDate"));
		configuration.addProperty("suppressAllComments", this.context.getProperty("suppressAllComments"));
		configuration.addProperty("addRemarkComments", this.context.getProperty("addRemarkComments"));
		configuration.addProperty("swagger2", this.context.getProperty("swagger2"));
		configuration.addProperty("baseModel", this.context.getProperty("baseModel"));
		this.context.setCommentGeneratorConfiguration(configuration);
		context.getConnectionFactoryConfiguration().addProperty("remarksReporting", "true");
	}

	// 不生成getter和setter
	@Override
	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return false;
	}

	@Override
	public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return false;
	}
}