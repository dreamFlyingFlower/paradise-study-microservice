

# Nacos



# 概述

* [官方地址](https://nacos.io)
* [github地址](https://github.com/alibaba/nacos)
* 是一个构建云原生应用的动态服务发现,配置管理和服务管理平台
* 常见的注册中心:Eureka,Zookeeper,Consul,Nacos
* Nacos=Spring Cloud Eureka + Spring Cloud Config
* Nacos可以与Spring,Spring Boot,Spring Cloud集成,并能代替Spring Cloud Eureka,Spring Cloud Config

- 通过Nacos Server和spring-cloud-starter-alibaba-nacos-config 实现配置的动态变更
- 通过Nacos Server和spring-cloud-starter-alibaba-nacos-discovery 实现服务的注册与发现
- Nacos是以服务为主要服务对象的中间件,Nacos支持所有主流的服务发现,配置和管理
- Nacos用于服务发现和服务健康监测,动态配置服务,动态DNS服务,务及其元数据管理



# 快速开始



![](image01.png)



## 下载及安装

你可以通过源码和发行包两种方式来获取Nacos

您可以从[最新稳定版本](https://github.com/alibaba/nacos/releases)下载 `nacos-server-$version.zip` 包



## 启动服务

* linux:sh startup.sh -m standalone,standalone代表着单机模式运行,非集群模式
* Windows:cmd startup.cmd或双击startup.cmd
* 访问:http://localhost:8848/nacos,用户名密码:nacos/nacos



## 数据支持

* 单机模式时Nacos默认使用嵌入式数据库存储数据,若想使用其他存储方式,需要进行配置
* MySQL中新建nacos_config数据库,Nacos初始化MySQL的文件在nacos/conf/nacos-mysql.sql
* 修改nacos/conf/application.properties,增加支持MySQL数据源配置

```properties
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://localhost:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.username=root
db.password=root
```

* 配置完成后重启Nacos



# 注册中心

首先创建两个工程:生产者->nacos-provider,消费者->nacos-consumer



## 生产者

ProviderController:

```java
@RestController
public class ProviderController {

    @Value("${myName}")
    private String name;

    @GetMapping("hello")
    public String hello(){
        return "hello " + name;
    }
}
```

application.properties配置如下:

```properties
server.port=18070
# 自定义参数
myName=nacos
```



## 注册到nacos

* 添加依赖:spring-cloud-starter-alibaba-nacos-discovery及springCloud

* 在application.properties中配置nacos服务地址和应用名

  ```yaml
  server.port=8070
  spring.application.name=nacos-provider
  # nacos服务地址
  spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
  # 自定义参数
  myName=nacos
  ```

* 通过Spring Cloud原生注解@EnableDiscoveryClient开启服务注册发现功能

  ```java
  @SpringBootApplication
  @EnableDiscoveryClient
  public class NacosProviderApplication {
      public static void main(String[] args) {
          SpringApplication.run(NacosProviderApplication.class, args);
      }
  }
  ```

* 页面查看如下

![](image02.png)



## 消费端

ConsumerController:

```java
@RestController
public class ConsumerController {

    @GetMapping("hi")
    public String hi() {
        return "hi provider!";
    }
}
```

application.properties:

```properties
server.port=18080
```

注册到Nacos的步骤同生产者,注册成功后在Nacos管理界面有会2个服务



## 使用feign调用服务



在消费者的NacosConsumerApplication类上添加@EnableFeignClients注解:

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NacosConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

}
```

编写ProviderFeign如下:

```java
@FeignClient("nacos-provider")
public interface ProviderFeign {

    @RequestMapping("hello")
    public String hello();
}
```

在Controller中使用feignClient:

```java
@RestController
public class ConsumerController {

    @Autowired
    private ProviderFeign providerFeign;

    @GetMapping("hi")
    public String hi() {
        return this.providerFeign.hello();
    }
}
```

测试访问成功



# 配置中心



* Nacos配置中心:系统配置的集中管理,包括编辑,存储,分发,动态更新不重启,回滚配置(变更管理,历史版本管理,变更审计)等所有与配置相关的活动
* Nacos配置中心的作用等同于SpringCloudConfig



## 创建配置

进入配置管理->配置列表->新建配置

![](image03.png)

* dataId:完整格式如下->${prefix}-${spring.profile.active}.${file-extension}

- prefix:默认为所属工程配置spring.application.name的值,即nacos-provider,也可以通过配置项spring.cloud.nacos.config.prefix来配置
- spring.profile.active:当前环境对应的profile,详情可以参考 
- 当spring.profile.active为空时,dataId变成${prefix}.${file-extension}
- file-exetension:配置内容的数据格式,可以通过配置项spring.cloud.nacos.config.file-extension来配置



## 读配置



* 引入依赖:spring-cloud-starter-alibaba-nacos-config
* 在bootstrap.properties中配置Nacos server的地址和应用名,注意必须是bootstrap文件

```properties
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
# 该配置影响统一配置中心中的dataId,之前已经配置过
spring.application.name=nacos-provider
```

* 之所以需要配置spring.application.name,是因为它是构成Nacos配置管理dataId字段的一部分
* 通过Spring Cloud注解@RefreshScope实现配置自动更新:

```java
@RestController
@RefreshScope
public class ProviderController {@Value("${myName}"){
    private String name;

    @RequestMapping("hello")
    public String hello(){
        return "hello " + name;
    }
}
```



## 名称空间

* 开发环境较多时(默认只有public),可以根据不同环境来创建不同的namespce,进行多环境的隔离

![1567300201637](image04.png)

* 当有多个命名空间时,切换到配置列表页面,可以看到最上面所有的命名空间,点击可查看他们的id
* 在配置列表页面,只有public是默认存在的,其他都是自己新建的
* 默认情况下,项目会到public下找服务名.properties文件
* 在dev名称空间中也添加一个nacos-provider.properties配置.这时有两种方式:
  * 切换到dev名称空间,添加一个新的配置文件.缺点:每个环境都要重复配置类似的项目
  * 直接通过clone方式添加配置,并修改即可

![](image05.png)

![](image06.png)

![](image07.png)

* 点击编辑:修改配置内容,以作区分
* 在服务提供方nacos-provider中切换命名空间,修改bootstrap.properties添加如下配置

```properties
# 该值为配置管理->配置列表各个配置下的uuid
spring.cloud.nacos.config.namespace=7fd7e137-21c4-4723-a042-d527149e63e0
```

* namespace的值为:

![](image08.png)



## 回滚配置

* **目前版本该功能有bug,回滚之后配置消失.**
* 配置中心点击更多可查看,回滚配置只需要两步:
  * 查看历史版本
  * 回滚到某个历史版本



## 多配置文件

偶尔情况下需要加载多个配置文件.假如现在dev名称空间下有三个配置文件:nacos-provider.properties,redis.properties,jdbc.properties

![1567305611637](image09.png)

nacos-provider.properties默认加载,在bootstrap.properties文件中添加如下配置:

```properties
spring.cloud.nacos.config.ext-config[0].data-id=redis.properties
# 开启动态刷新配置,否则配置文件修改,工程无法感知
spring.cloud.nacos.config.ext-config[0].refresh=true
spring.cloud.nacos.config.ext-config[1].data-id=jdbc.properties
spring.cloud.nacos.config.ext-config[1].refresh=true
```

修改ProviderController使用redis.properties和jdbc.properties配置文件中的参数:

```java
@RestController
@RefreshScope
public class ProviderController {

    @Value("${myName}")
    private String name;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${redis.url}")
    private String redisUrl;

    @RequestMapping("hello")
    public String hello(){
        return "hello"+name+",redis-url="+redisUrl+",jdbc-url=" + jdbcUrl;
    }
}
```



## 配置分组

* 在实际开发中,除了不同的环境外,不同的微服务或者业务功能,可能有不同的redis及mysql数据库
* 区分不同的环境我们使用namespace,区分不同的微服务或功能,使用group(分组)

![](image10.png)

* 现在开发环境中有两个redis.propertis配置文件,一个是DEFAULT_GROUP,一个是provider
* 默认从DEFAULT_GROUP读取redis配置,如果要切换到provider分组,需要添加如下配置

```properties
# 指定分组
spring.cloud.nacos.config.ext-config[0].group=provider
```


