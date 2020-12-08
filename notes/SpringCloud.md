# Springcloud
-------
# 一.项目结构

## 1.1 CloudServer-5500

	服务端,注册服务,监听注册了服务的程序是否存活,同时也可以注册在别的server上,利用eureke或zookeeper进行注册.在整个模块中,各个模块都是以项目的名字来做标识的,即spring.applicaton.name的值,他们只是ip和端口不同,这样可以更好的进行分布式部署,只需要name相同,可很简单的实现内部负载均衡

## 1.2CloudConfig-5600
	分布式配置管理,可以将自定义配置放在github或者gitlab上,自动刷新配置
	config:config的服务端配置

## 1.3 CloudClient1-5510
	各种业务程序,其他组件的演示
	mybatis
	activiti:流程,和jbpm一样
	webservice
	redis

## 1.4 CloudClient-5511
	微服务组件,同CloudClient1
	config:客户端的使用,server端在cloudconfig组件中示例
	nutz:使用CloudServer2的控制层代码

## 1.5 CloudService2
	业务代码分拆,将service层拆分出去,可供其他组件使用
	nutz:nutz的service层

## 1.6 CloudFeign-5520
	ribbon:负载均衡,api被调用的几率可根据需要自由配置
	Feign负载均衡,保证某个微服务挂掉,仍然维持服务的正常运行.Feign集成了ribbon以及hystrix
	Hystrix则保证其中一个微服务挂掉时,大量请求不会因为该服务挂掉而不停的消耗资源,以至于拖跨整个服务
	actuator

## 1.7 CloudGateway-5530
	路由配置,url统一转发,拦截等.有2中方式:gateway是spring亲儿子,zuul是springcloud2.0版本以前使用的
	spring-gateway
	zuul

## 1.8 CloudZipkin-5540
	链路追踪,用来分析接口在每个调用阶段所用的时间以及调用情况,更好的改造程序
	spring-sleuth

## 1.9 CloudKafka
	kafka的消息队列管理,跟ActiveMQ,RabbitMQ是一样的功能,只是在实现上有所不同.
* 使用kafka必须先配置[zookeeper](https://www.cnblogs.com/shanyou/p/3221990.html)

## 1.10 CloudSecuriy-5550
	spring自带的安全组件,利用session来验证用户是否登录,以及角色权限等
	security
	social:第三方登录
	oauth2:登录协议

## 1.11 CloudWsdl-5570
	各种调用别人的接口以及调用webservice接口,自己写的服务端因为需要连接数据库,放在CloudClient1中

## 1.12 CloudMQ-5580
	rabbitmq消息队列,高并发限流,请求处理,类似kafuka,可分布式部署
	rabbitmq
	activemq
	stream

## 1.13 CloudSearch-5590
	Solr以及Elasticsearch搜索功能,Solr是传统企业级搜索,Elasticsearch是实时搜索

## 1.14 CloudOAuthServer-5610
	OAuth2



# 二.分布式的优缺点

1. 测试容易
2. 可伸缩性强,可靠性强
3. 跨语言更灵活
4. 团队容易协作
5. 系统迭代容易
6. 维护成本高,部署量多
7. 接口兼容多版本
8. 分布式系统的复杂性
9. 分布式事务



# 三.微服务设计原则

1. 尽量不要A服务中的sql连接查询到B服务中的表等情况,这样在A服务与B服务进行垂直拆库时会报错
2. 服务子系统间避免出席那环状的依赖调用
3. 服务子系统间的依赖关系链不要过长
4. 尽量避免分布式事务
5. 单一职责原则
6. 服务自治原则
7. 轻量级通信原则
8. 接口明确原则



# 四.分布式事务

1. TCC:分布式事务框架,比较复杂
2. 消息队列,利用ACK机制和定时任务机制
3. 可靠事务的补偿机制



# 五.各种监控以及文档

* server:当开启了server服务端的时候,可在网页上打开ip:port直接查看相关信息
* actuator:需添加spring-boot-starter-actuator包,在浏览器上查看的方式是ip:port/actuator,需要相关配置

>
	2.0版本访问actuator相关url地址时,若需要在控制台看到相关可访问url,需要重新指定日志级别
	logging.level.org.springframework.boot.actuate=trace
	若是同时使用了spring-security,则同时需要开启spring-security的配置,否则访问需要权限
	spring.security.filter.dispatcher-types= include

* hystrix:断路器接口信息,查看方式ip:port/hystrix,填写相关信息
* druid:ip:port/druid,若配置了loginusername和loginpassword,则登录该网站的时候需要用户名和密码
* swagger2:ip/port/swagger-ui.html#/;swagger2生成的在线api文档
* springbootadmin:spring可视化监控报表,非spring官方组件



# 六.学习

## 6.1 sharding

数据库分库分表解决方案,见CloudClient2或SimpleOA项目



## 6.2 javaagent

直接对底层字节码文件修改,在类加载时动态拦截并重新修改class字节码,插入监听指令



## 6.3 javassist

直接对底层字节码文件修改,在类加载时动态拦截并重新修改class字节码,插入监听指令



## 6.4 swagger2

在线文档生成,访问地址为ip:port/swagger-ui.html#/

```java
<!-- 因为一些版本bug,需要添加更高版本的jar包 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
    <exclusions>
        <exclusion>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-schema</artifactId>
        </exclusion>
        <exclusion>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-spring-web</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-schema</artifactId>
    <version>2.9.2</version>
</dependency>
```



## 6.5 Jenkins

持续继承,自动化部署,需要先安装git,maven

# 七.其他技巧



## 7.1 更换maven镜像

```java
<mirror>
    <id>Central</id>
    <mirrorOf>*</mirrorOf>
    <name>maven</name>
    <url>http://repo1.maven.org/maven2/</url>
</mirror>
```



## 7.2 Spring样例下载

1. spring需要先在官网上下载依赖以及相关的配置,如果已经有过相同的代码,则不需要再下载[样例下载地址](https://start.spring.io/)
2. 在下载样例的网页上,选择依赖的时候,可以点击下方的switch to the full version来查看spring关联的主流依赖,也可直接搜索
3. 下载解压后直接导入到开发工具中
4. 直接在工具中使用springboot的maven镜像



## 7.3 springboot

### 7.3.1 目录结构

1. src/mian/java:主要的代码书写资源文件夹,源码目录
2. src/main/resouces:资源配置文件存放目录
   1. static:默认静态资源存放目录,如html.js等文件.可在配置文件中修改地址
   2. templates:模版配置文件存放目录,freemarker使用
   3. 其他配置文件,如日志文件,mybatis的mapper文件
3. src/test/java:测试目录



### 7.3.2 配置文件

1. 若需要详细查看配置文件有那些固定属性,可查看[文档](https://docs.spring.io/spring-boot/docs/2.0.4.BUILD-SNAPSHOT/reference/htmlsingle)
2. springboot的配置文件只能是application.properties或application.yml.可安装yml文件提示插件,点开eclipse的help->about eclipse查看eclipse的版本号
3. 打开[eclipse的下载地址](https://spring.io/tools/sts/legacy),下载已经集成了sts的eclipse
4. 也可以直接下载[sts的插件](https://spring.io/tools/sts/all),需要根据相应的版本来选择,否则插件会出错
5. 在配置文件中,可以使用---表示一种情况,profiles表示配置多种启动环境



### 7.3.3 配置文件加载

1. 默认是加载application.yml,找不到application.yml会报错
2. 若在resources目录下有bootstrap.yml,那么先加载bootstrap.yml
3. 若有多个配置文件,可直接在application-后面加字符即可,如application-dev.yml
4. 在application中配置其他配置文件是否加载,参数为spring.profiles.active,该参数的类型为list,值为application-的后缀,如加载application-dev.yml,只需要写dev即可
5. 配置文件加载顺序:如将项目打成jar包,则先加载项目.jar同层目录的config目录中的配置文件->jar包中的resources目录下的config目录中的配置文件->resources目录中的配置文件



### 7.3.4 jar运行

1. 直接运行

   ```shell
   # 直接以项目中默认的配置运行
   java -jar xxx.jar
   ```

2. 加配置参数

   ```shell
   # 加上配置文件中的参数运行,需要以--开头,值则是配置文件的值
   # 开发环境运行,dev为applicaiton-dev.yml
   java -jar xxx.jar --spring.profiles.active=dev
   # 生产环境,prod为application-prod.yml
   java -jar xxx.jar --spring.profiles.active=prod
   # linux后台运行
   nohup java -jar xxx.jar --spring.profiles.active=prod & 表示后台运行jar
   ```



## 7.4 导入本地jar包

```java
// 需要在控制台输入命令,先要配置环境变量
mvn install:install-file -DgroupId=com.wy -DartifactId=java-utils -Dversion=0.1 -Dfile=E:\jicheng\xxxx-0.1.0.jar -Dpackaging=jar
// Dfile:指明需要导入的jar包的本地地址
// DgroupId,DartifactId,Dversion:顾名思义是值依赖里的三项,随意填写
```



## 7.5 mysql时区错误

1. 直接在url连接的后面加上serverTimezone=GMT或加上serverTimezone=GMT%2B8(加8个时区)
2. 修改mysql的配置文件my.ini,在mysqld结点加上default-time-zone='+08:00',之后重启mysql
3. 进入mysql的控制台,直接sql语句set global time_zone='+8:00'

