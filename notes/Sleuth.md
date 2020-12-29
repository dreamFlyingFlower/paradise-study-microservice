# Sleuth



# 概述

* 分布式系统链路追踪,通常和zipkin一起使用



# 使用

* 服务提供者和消费者都需要添加spring-cloud-sleuth的依赖

* 打开debug日志

  ```properties
  logging.level.org.springframework.cloud.openfeign=debug
  logging.level.org.springframework.cloud.sleuth=debug
  ```

* 发起调用即可在控制台看到链路信息

  ```
  # 依次是服务名,链路编号(一条链路只有一个),链路基本单元,是否输出到其他服务(如zipkin)
  DEBUD [service-name,tranceid,spanid,false]
  ```

* 使用zipkin之后会自动依赖sleuth

* 

# Zipkin

* Sleuth数据的可视化界面

* 下载zipkin的运行程序,设置端口,启动服务,默认端口是9411

* 项目中添加zipkin的依赖,会自动依赖sleuth

* 项目配置文件中配置zipkin服务地址

  ```properties
  spring.zipkin.base-url=
  # 关闭服务发现,否则springcloud会把zipkin的url当服务名称
  spring.zipkin.discoveryClientEnabled=false
  # 设置传输数据的方式
  spring.zipkin.sender.type=web
  # 设置抽样采集率为100%,0.1就是10%
  spring.sleuth.sampler.probability=1
  ```

* zipkin的数据默认是存放在内存中,可以根据业务需求进行持久化:如mysql,es,cassandra等