package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

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
 * @apiNote Feign:内部负载均衡,2个完全相同的项目,除了ip和端口不同,提供给前端调用的接口都相同,name要相同
 *          Hystrix:断路器,防止因为一个服务挂掉,其他服务全部挂掉的服务
 *          Actuator:spring服务监控,见CloudFeign.https://blog.csdn.net/hanghangaidoudou/article/details/81141473
 *          AdminUI:配合Actuator使用,可直接形成图形化界面<br>
 *          ZUUL:外部负载均衡,前端调用后台接口负载,网关<br>
 *          Gateway:作用等同于zuul,是spring官方支持的,需要spring5.0,boot2.0<br>
 *          Springloaded:热部署,和devtool不同,该jar包不会重新部署程序,而是以线程的方式在后台运行,对html的修改无法监控
 *          nginx+varnish:实现动静分离,提高前端web运行速度.实现反向代理,web加速 metrics:集群监控<br>
 *          sleuth:服务链路追踪<br>
 * 
 * @apiNote eureka在2.0闭源,服务注册与发现可更换为其他组件,例如zookeeper,nacos,Consul,见CloudServer1
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