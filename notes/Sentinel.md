# Sentinel

# 概述

* [官网](https://github.com/alibaba/Sentinel/wiki)



# 整合SpringBoot

* 去官网下载对应版本的SentinelWeb控制台的jar包到本地,通过控制台启动jar包
* 项目中导入依赖spring-cloud-starter-alibaba-sentinel
* 在项目中配置SentinelWeb的地址信息.控制台是懒加载,只有发送了请求才有数据
* 在控制台调整参数,默认所有的流控设置保存在内存中,重启失效
* 每个微服务中都导入spring-actuator,放开监控限制
* 编写Sentinel的配置,使用WebCallbackManager类自定义流控



## 熔断Feign

* 使用Sentinel保护feign远程调用的熔断
* 调用方的熔断保护,需要开启feign的sentinel监控:配置文件中添加feign.sentinel.enabled=true
* 调用方手动指定远程调用服务失败后的降级策略:需要在sentinel的web管理界面操作
* 超大浏览的时候,必须牺牲一些远程服务,在服务的提供方(远程服务)指定降级策略



## 限流Gateway

* 添加相关依赖,在Sentinel的管理界面中可以看到网关的管理界面,和其他服务不同



## 自定义保护资源

* 可以在方法上使用注解@SentinelResource("自定义资源的名称")

  ```java
  @SentinelResource(value="自定义资源的名称",blockHandler="限流之后的处理方法名")
  public void test(){
  }
  ```

* 或者将需要保护的资源放入到try-catch块中,同时在try中定义资源,作用和注解相同如

  ```java
  try(Entry entry = SphU.entry("自定义资源的名称")){
  }catch{
  }
  ```

* 当该资源被调用过之后,会自动添加到Sentinel的管理界面中

* 可以手动添加限流或降级的资源,只需要名称和自定义资源的名称相同即可



# 比较Hystrix

* 功能:Sentinel->信号量隔离,并发线程数限流;Hystrix->线程池隔离,信号量隔离
* 熔断降级策略:Sentinel->基于响应时间,异常比例,异常数;Hystrix->基于异常比例
* 实时统计实现:Sentinel->滑动窗口(LeapArray);Hystrix->滑动窗口(基于RxJava)
* 动态规则配置:Sentinel->支持多种数据源;Hystrix->支持多种数据源
* 扩展性:Sentinel->多个扩展点;Hystrix->插件形式
* 基于注解的支持:都支持
* 限流:Sentinel->基于QPS支持基于调用关系的限流;Hystrix->有限的支持
* 流量整形:Sentinel->支持预热模式,匀速器模式,预热排队模式;Hystrix->不支持
* 系统自适应保护:Sentinel->支持;Hystrix->不支持
* 控制台:Sentinel->可配置队则,查看秒级监控,机器发现等;Hystrix->简单的监控查看



# Hystrix
