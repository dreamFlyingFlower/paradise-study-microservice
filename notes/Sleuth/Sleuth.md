# Sleuth



# 概述

* 分布式系统链路追踪,通常和zipkin一起使用
* 它有助于收集解决微服务架构中的延迟问题所需的时序数据,并管理这些数据的收集和查找
* ZipkinUI提供了一个依赖关系图,显示了每个应用程序通过的跟踪请求数
* 如果要解决延迟问题或错误,可以根据应用程序,跟踪长度,注释或时间戳对所有跟踪进行筛选或排序
* 选择跟踪后,可以看到每个跨度所需的总跟踪时间百分比,从而可以识别有问题的应用程序



# 核心

## Span

* 基本工作单元发送一个远程请求就会产生一个span
* span通过一个64位ID唯一标识,trace以另一个64位ID表示
* span还有其他数据信息,比如摘要,时间戳事件,关键值注释(tags),span的ID,进度ID(通常是IP)
* span在不断的启动和停止,同时记录了时间信息,当你创建了一个span,必须在未来的某个时刻停止



## Trace

* 一系列spans组成的一个树状结构
* 发送一个请求需要调用多个微服务,每调用一个微服务都会产生一个span,这些span组成一个trace



## Annotation

* 用来及时记录一个事件的存在,一些核心annotations用来定义一个请求的开始和结束

- cs:Client Sent,客户端发起一个请求,这个annotion描述了这个span的开始

- sr:Server Received,服务端获得请求并准备处理它,如果将其sr减去cs时间戳便可得到网络延迟

- ss:Server Sent,表明请求处理的完成(当请求返回客户端),如果ss减去sr时间戳便可得到服务端需要的处理请求时间

- cr:Client Received,表明span的结束,客户端成功接收到服务端的回复,如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间

  

## 示例

请求如下

![dependency](image03.png)

使用zipkin跟踪整个请求过程如下

![img](image04.webp)

上图表示一请求链路,一条链路通过TraceId唯一标识,`Span`标识发起的请求信息,各`span`通过`parent id` 关联起来
![](image05.png)





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




# Zipkin

* Sleuth数据的可视化界面

* [下载](https://search.maven.org/remote_content?g=io.zipkin&a=zipkin-server&v=LATEST&c=exec)zipkin的运行程序,设置端口

* 启动服务java -jar zipkin-server-*exec.jar,默认端口是9411

* 浏览器访问http://localhost:9411

* 项目中添加zipkin的依赖,会自动依赖sleuth

* 项目配置文件中配置zipkin服务地址

  ```properties
  spring.zipkin.base-url=http://localhost:9411
  # 关闭服务发现,否则springcloud会把zipkin的url当服务名称
  spring.zipkin.discoveryClientEnabled=false
  # 设置传输数据的方式
  spring.zipkin.sender.type=web
  # 设置抽样采集率为100%,0.1就是10%
  spring.sleuth.sampler.probability=1
  ```

* zipkin的数据默认是存放在内存中,可以根据业务需求进行持久化:如mysql,es,cassandra等

* 访问http://localhost:18080/hi,查看Zipkin页面

![](image01.png)

![](image02.png)



# Docker

* docker run -d -p 9411:9411 openzipkin/zipkin