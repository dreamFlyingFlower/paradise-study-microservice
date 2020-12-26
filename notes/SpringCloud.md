# Springcloud
-------
# 项目结构



## paradise-microservice-server

	服务端,注册服务,监听注册了服务的程序是否存活,同时也可以注册在别的server上,利用eureke或zookeeper进行注册.在整个模块中,各个模块都是以项目的名字来做标识的,即spring.applicaton.name的值,他们只是ip和端口不同,这样可以更好的进行分布式部署,只需要name相同,可很简单的实现内部负载均衡



## paradise-microservice-config

	分布式配置管理,可以将自定义配置放在github或者gitlab上,自动刷新配置
	config:config的服务端配置



## paradise-microservice-client

	微服务组件,同CloudClient1
	config:客户端的使用,server端在cloudconfig组件中示例
	mybatis
	activiti:流程,和jbpm一样
	webservice
	redis



## paradise-microservice-service

	业务代码分拆,将service层拆分出去,可供其他组件使用



## paradise-microservice-feign

	ribbon:负载均衡,api被调用的几率可根据需要自由配置
	Feign负载均衡,保证某个微服务挂掉,仍然维持服务的正常运行.Feign集成了ribbon以及hystrix
	Hystrix则保证其中一个微服务挂掉时,大量请求不会因为该服务挂掉而不停的消耗资源,以至于拖跨整个服务
	actuator



## paradise-microservice-gateway

	路由配置,url统一转发,拦截等.有2中方式:gateway是spring亲儿子,zuul是springcloud2.0版本以前使用的
	spring-gateway
	zuul



## paradise-microservice-zipkin

	链路追踪,用来分析接口在每个调用阶段所用的时间以及调用情况,更好的改造程序
	spring-sleuth

## 
## paradise-microservice-security
	spring自带的安全组件,利用session来验证用户是否登录,以及角色权限等
	security
	social:第三方登录
	oauth2:登录协议



## paradise-microservice-wsdl

	各种调用别人的接口以及调用webservice接口,自己写的服务端因为需要连接数据库,放在CloudClient1中



## paradise-microservice-amqp

	rabbitmq消息队列,高并发限流,请求处理,类似kafuka,可分布式部署
	rabbitmq
	activemq
	stream
	kafka的消息队列管理,跟ActiveMQ,RabbitMQ是一样的功能,只是在实现上有所不同.

* 使用kafka必须先配置[zookeeper](https://www.cnblogs.com/shanyou/p/3221990.html)



## paradise-microservice-search

	Solr以及Elasticsearch搜索功能,Solr是传统企业级搜索,Elasticsearch是实时搜索



## paradise-microservice-oauth

	OAuth2



# 分布式

1. 测试容易
2. 可伸缩性强,可靠性强
3. 跨语言更灵活
4. 团队容易协作
5. 系统迭代容易
6. 维护成本高,部署量多
7. 接口兼容多版本
8. 分布式系统的复杂性
9. 分布式事务



# 微服务设计原则

1. 尽量不要A服务中的sql连接查询到B服务中的表等情况,这样在A服务与B服务进行垂直拆库时会报错
2. 服务子系统间避免出席那环状的依赖调用
3. 服务子系统间的依赖关系链不要过长
4. 尽量避免分布式事务
5. 单一职责原则
6. 服务自治原则
7. 轻量级通信原则
8. 接口明确原则



# 分布式事务

1. TCC:分布式事务框架,比较复杂
2. 消息队列,利用ACK机制和定时任务机制
3. 可靠事务的补偿机制
4. 阿里的seata



# 各种监控

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



# 学习

## Sharding

数据库分库分表解决方案,见CloudClient2或SimpleOA项目



## Javaagent

直接对底层字节码文件修改,在类加载时动态拦截并重新修改class字节码,插入监听指令



## Javassist

直接对底层字节码文件修改,在类加载时动态拦截并重新修改class字节码,插入监听指令



## Swagger2

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



## Jenkins

持续继承,自动化部署,需要先安装git,maven

# 其他技巧



## 更换maven镜像

```java
<mirror>
    <id>Central</id>
    <mirrorOf>*</mirrorOf>
    <name>maven</name>
    <url>http://repo1.maven.org/maven2/</url>
</mirror>
```



## Spring样例下载

1. spring需要先在官网上下载依赖以及相关的配置,如果已经有过相同的代码,则不需要再下载[样例下载地址](https://start.spring.io/)
2. 在下载样例的网页上,选择依赖的时候,可以点击下方的switch to the full version来查看spring关联的主流依赖,也可直接搜索
3. 下载解压后直接导入到开发工具中
4. 直接在工具中使用springboot的maven镜像



## Springboot

### 目录结构

1. src/mian/java:主要的代码书写资源文件夹,源码目录
2. src/main/resouces:资源配置文件存放目录
   1. static:默认静态资源存放目录,如html.js等文件.可在配置文件中修改地址
   2. templates:模版配置文件存放目录,freemarker使用
   3. 其他配置文件,如日志文件,mybatis的mapper文件
3. src/test/java:测试目录



### 配置文件加载

1. 默认是加载application.yml,找不到application.yml会报错
2. 若在resources目录下有bootstrap.yml,那么先加载bootstrap.yml
3. 若有多个配置文件,可直接在application-后面加字符即可,如application-dev.yml
4. 在application中配置其他配置文件是否加载,参数为spring.profiles.active,该参数的类型为list,值为application-的后缀,如加载application-dev.yml,只需要写dev即可
5. 配置文件加载顺序:如将项目打成jar包,则先加载项目.jar同层目录的config目录中的配置文件->jar包中的resources目录下的config目录中的配置文件->resources目录中的配置文件



### Jar运行

1. 直接运行

   ```shell
   # 直接以项目中默认的配置运行
   java -jar xxx.jar
   ```

2. 加配置参数

   ```shell
   # 加上配置文件中的参数运行,需要以--开头,值则是配置文件的值
   # 生成环境运行,prod为applicaiton-prod.yml
   java -jar xxx.jar --spring.profiles.active=prod
   # linux后台运行,&表示后台运行,nohup会在当前目录产生一个日志文件
   nohup java -jar xxx.jar --spring.profiles.active=prod &
   ```



## 导入本地jar包

```java
// 需要在控制台输入命令,先要配置环境变量
mvn install:install-file -DgroupId=com.wy -DartifactId=java-utils -Dversion=0.1 -Dfile=E:\jicheng\xxxx-0.1.0.jar -Dpackaging=jar
// Dfile:指明需要导入的jar包的本地地址
// DgroupId,DartifactId,Dversion:顾名思义是值依赖里的三项,随意填写
```



## MySQL时区错误

* 在url后加上serverTimezone=GMT或serverTimezone=GMT%2B8(加8个时区)或serverTimezone=Asia/ShangHai
* 修改mysql配置文件my.ini,在mysqld节点加上default-time-zone='+08:00',重启mysql
* 进入mysql的控制台,直接sql语句set global time_zone='+8:00'



## 接口幂等性

* 用于对于同一操作发起的一次请求或多次请求的结果是相同的,不会因为多次点击而产生副作用,例如支付,银行业务等
* token机制:服务端提供发送token的接口
  * 分析哪些业务是存在幂等性的,这些业务就必须在执行之前,先去获取token,服务器会把token保存到缓存中
  * 之后调用业务接口请求时,把token携带过去,一般放在请求头中
  * 服务器判断token是否存在于缓存中,存在则表示第一次请求,然后删除token
  * 如果token不存在,表示是重复操作,直接返回重复标记给客户端
  * 若先删除token,可能会出现业务确实没有执行,重试还是之前的token,业务不能继续
  * 后删除token可能存在服务异常,没有删除缓存中的token,仍然会请求2次业务
  * 若对幂等性要求不高,可以先删除token,客户端业务调用失败,可以给提示,让客户端重新获取token,进行二次提交
  * token的获取,比较,删除必须都是原子性才能完全保证幂等性.若缓存使用的redis,可以使用set方式设置值,删除时用lua脚本,详见redis官网的set方法,非setnx
* 数据库悲观锁:select 1 from tablename where id=1 for update;这种查询方式会进行锁行,同时id字段必须是主键或索引字段,否则可能造成锁表
* 数据库乐观锁:更新某个字段的状态值,更新成功表示执行正确的操作,更新失败表示已经执行
* 业务层分布式锁:处理数据之前需要先判断数据是否被处理过
* 数据库的各种唯一约束:如主键约束,但是必须保证在分布式下全局唯一
* redis set防重:计算数据的MD5放入redis的set集合中,每次处理数据查看MD5是否存在
* 全局请求唯一id:接口被调用时,生成一个唯一id,redis将数据保存到set中,存在即处理过