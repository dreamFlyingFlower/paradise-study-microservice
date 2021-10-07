package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.support.DefaultLifecycleProcessor;

/**
 * SpringCloud服务注册中心,不同服务之间需要通过spring.application.name辨识和注册
 * 
 * {@link EnableEurekaServer}:该注解表明当前项目是一个服务注册中心,添加到启动类即可启动
 * 启动完成之后,网页输入ip:port即可打开前端监控页面,查看当前注册中心有哪些服务注册,服务状态等
 * 注册中心有多种实现,可以使用zookeeper,nacos等代替eureka,需要导入相关jar包
 * 
 * 为了实现注册服务的高可用,可以复制注册服务,修改端口即可;在client注册多个服务,逗号隔开
 * 多个注册服务都启动的时候,client默认是连接第一个注册服务,第一个注册服务挂掉才会连接其他注册服务
 * 
 * Eureka自我保护机制:
 * 
 * <pre>
 * 该机制是注册中心的重要特性,当Eureka注册中心进入自我保护模式时,在Eureka Server首页会输出如下警告信息:
 * EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT.
 * RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.
 * 在没有Eureka自我保护的情况下,如果Eureka Server在一定时间内没有接收到某个微服务的心跳,Eureka Server将会注销该实例.
 * 但是当发生网络分区故障时,微服务与Eureka Server之间将无法正常通信,以上行为可能变得非常危险.
 * 因为微服务本身是正常的,此时不应该注销这个微服务.如果没有自我保护机制,Eureka Server就会将此服务注销掉.
 * 当Eureka Server在短时间内丢失过多客户端时(可能发生了网络故障),就会把这个微服务节点进行保护,这就是自我保护机制.
 * 一旦进入自我保护模式,Eureka Server就会保护服务注册表中的信息,不删除服务注册表中的数据(也就是不会注销任何微服务).
 * 当网络故障恢复后,该Eureka Server会再自动退出自我保护模式.
 * 所以,自我保护模式是一种应对网络异常的安全保护措施,它的架构哲学是宁可同时保留所有微服务(健康的微服务和不健康的微服务都会保留),
 * 也不盲目注销任何健康的微服务,使用自我保护模式,可以让Eureka集群更加的健壮,稳定
 * 
 * 禁用自我保护模式:eureka.server.enable-self-preservation = false
 * 关闭自我保护模式后会出现红色:
 * THE SELF PRESERVATION MODE IS TURNED OFF. 
 * THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.
 * 
 * Eureka自我保护模式也会带来一些困扰.
 * 如果在保护期内某个服务提供者刚好非正常下线了,此时服务消费者就会拿到一个无效的服务实例,此时会调用失败.
 * 对于这个问题需要服务消费者端具有一些容错机制,如重试,断路器等.
 * Eureka的自我保护模式是有意义的.该模式被激活后,它不会从注册列表中剔除因长时间没收到心跳导致注册过期的服务,而是等待修复,
 * 直到心跳恢复正常之后,它自动退出自我保护模式.这种模式旨在避免因网络分区故障导致服务不可用的问题
 * </pre>
 * 
 * 
 * SpringCloud的常用组件:
 * 
 * <pre>
 * Feign:客户端负载均衡,2个完全相同的项目,除了ip和端口不同,提供给前端调用的接口都相同,spring.application.name要相同
 * Ribbon:客户端负载均衡,已经被Feign继承
 * Hystrix:断路器,防止因为一个服务挂掉,其他服务全部挂掉的服务
 * Actuator:spring服务监控. https://blog.csdn.net/hanghangaidoudou/article/details/81141473
 * AdminUI:配合Actuator使用,可直接形成图形化界面
 * ZUUL:客户端网关调用,前端调用后台接口负载,网关
 * Gateway:作用等同于Zuul,是spring官方支持的,需要spring5.0,boot2.0
 * Springloaded:热部署,和devtool不同,该jar包不会重新部署程序,而是以线程的方式在后台运行,对html的修改无法监控
 * nginx+varnish:实现动静分离,提高前端web运行速度.实现反向代理,web加速 metrics:集群监控
 * Sleuth+Zipkin:服务链路追踪+图形界面监控
 * </pre>
 * 
 * 自动注册原理:
 * 
 * <pre>
 * {@link ServiceRegistry}:服务注册抽象,由服务端注册实现
 * {@link DiscoveryClient}:客户端发现抽象,由服务端实现
 * {@link EurekaServiceRegistry}{@link EurekaRegistration}:Eureka的服务发现实现以及注册,实现了ServiceRegistry
 * {@link EurekaAutoServiceRegistration}:Eureka服务端配置,由{@link DefaultLifecycleProcessor}调用
 * {@link EurekaClientAutoConfiguration}:发现服务注册的自动配置
 * </pre>
 * 
 * @apiNote eureka在2.0闭源,服务注册与发现可更换为其他组件,例如zookeeper,nacos,Consul
 *          若开启eureka集群,则只是server上配置文件的不同,可创建多个配置,启动不同的配置文件即可
 * 
 * @apiNote CAP:Consistency(一致性),Availability(可用性),PartitionTolerance(分区容错性),三者不可兼得
 *          eureka和zookeeper的区别,eureka为AP,因为eureka的程序都是平级的关系,不存在数据的主从复制,
 *          数据可能会产生不一致.eureka有服务保护机制,即当某个服务挂掉的时候,注册中心会暂时保存client的信息,
 *          当client重新注册的时候,服务讲仍然可用.分区容错在是通过每个服务部署多个来实现
 *          zk为CP,zk的leader是唯一的,在leader挂掉的时候服务不可用,因为要重新选举leader.
 *          zk的数据一致性是通过主从复制来实现的,从服务的数据都会发送到主服务,主服务会重新将数据发送到各个从服务
 *          分区容错则是通过leader挂掉的时候,重新选举leader来实现的
 * 
 * @apiNote 实现分布式锁,可使用zk的InterProcessMutex,用acquire获得锁,用release释放锁
 *          使用redis的incr方法同样可以实现自增长的线程安全数值.类似AtomicInteger
 * 
 * @apiNote 实现分布式事务,可以使用zk的临时节点
 * 
 * @author ParadiseWY
 * @date 2020-12-03 17:19:13
 * @git {@link https://github.com/mygodness100}
 */
@EnableEurekaServer
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}