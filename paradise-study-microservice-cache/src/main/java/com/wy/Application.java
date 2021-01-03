package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * Cache:缓存使用,若是直接引用了paradise-study-microservice-service,只会扫描本项目中的配置文件
 * 
 * Java定义了2个核心接口以及自动配置类:<br>
 * {@link CacheManager}:缓存管理器,管理各种Cache组件,统一不同的缓存技术,支持注解开发
 * {@link Cache}:缓存接口,定义缓存操作,常见实现有RedisCache,EhCacheCache,ConcurrentMapCache等<br>
 * {@link CacheAutoConfiguration}:缓存的自动配置类<br>
 * 每次调用需要缓存功能的方法时,spring会检查指定参数的指定目标方法是否已经被调用过:<br>
 * 如果有就直接从缓存中获取方法调用后的结果;如果没有则调用方法并缓存结果到缓存中后将结果返回前端
 * 
 * {@link EnableCaching}:若需要使用缓存,需要开启该注解,将缓存纳入到spring上下文中
 * 
 * {@link CacheConfig}:类上注解,指定类中通用属性.若方法上的属性和类上的属性重复,使用方法上的属性值
 * 
 * @apiNote {@link Cacheable}:该注解的value/cacheNames类似于命名空间,若不写,则默认为类的完整路径名+方法名
 *          value():当缓存使用ehcache时,该值表示使用哪种缓存策略,是配置文件中的cache标签的name属性
 *          key():缓存key值,可用SpEl表达式,不可用#result作为key,会有异常冲突.默认是所有参数的实际值
 *          methodName:当前被调用的方法名,如#root.methodName,可直接为#methodName,root固定写法
 *          method:当前被调用的方法,和methodName类似,#root.method;<br>
 *          target:当前被调用的目标对象,#root.target;<br>
 *          targetClass:被调用的目标对象类,#root.targetClass;<br>
 *          args:当前被调用的方法参数列表,#root.args,#root.args[0]...;<br>
 *          caches:当前方法调用使用的缓存列表,当value或cacheName有多值时,#root.caches[1].name表示第二个缓存的名称
 *          argname:被调用方法的参数名,可以是#参数名或#p0,等,0代表索引;<br>
 *          result:返回的结果集,不可使用#result做key.只有方法执行之后的判断有效,如unless等<br>
 *          keyGenerator():key的生成器,和key属性只能使用一个,需要实现{@link KeyGenerator}接口<br>
 *          condition():符合条件下才缓存,可以使用and或or连接多个判断<br>
 *          unless():否定缓存,和condition相反,符合条件就不缓存.可以使用#result获取结果进行额外判断<br>
 *          cacheManager():缓存管理器,指定从哪一个缓冲中取值<br>
 *          cacheResolver():同cacheManager,2者只能用一个<br>
 *          sync():是否使用异步模式,默认false同步,异步情况下不支持unless
 * 
 * @apiNote {@link CachePut}:该注解表示先调用方法,再更新缓存,基本用在数据库的更新操作.<br>
 *          更新缓存的时候需要注意,是否和查询使用的key一样,若不一样,原数据将不会更新,而是产生一个新的缓存
 * 
 * @apiNote {@link CacheEvict}:该注解清除缓存数据.和cacheput一样,也需要注意key的值要和查询的一样,
 *          否则查询相同的key时,仍可以从缓存中取得数据.默认是传参的值<br>
 *          allEntries():清除value/cachenames中所有的缓存,默认是false,此时key和keygenerator无效<br>
 *          beforeInvocation():是否在方法执行完之前清除缓存,默认false,之后清除
 * 
 * @apiNote {@link Caching}:该注解在复杂情况下使用,是上述3个注解的集合,可配置多种情况下的缓存操作
 * 
 * @apiNote spring-cache的不足:<br>
 *          读模式:<br>
 *          缓存穿透:在配置文件配置可以存储null数据,解决缓存穿透<br>
 *          缓存击穿:大量并发同时查询正好过期的数据,配置sync=true减少缓存击穿,分布式下不能完全解决<br>
 *          缓存雪崩:大量的key同时过期,加上随机的过期时间即可,最好是自定义缓存配置<br>
 *          写模式,缓存数据一致性,spring没有加任何锁:<br>
 *          读写加锁<br>
 *          引入Canal,感知到MySQL的更新去更新数据库<br>
 *          读多写多,直接去数据库查询即可<br>
 * 
 *          常规数据(读多写少,即时性,一致性要求不高的数据),完全可以使用spring-cache,写模式下只要缓存有过期时间即可<br>
 *          特殊数据需要特殊设计
 *
 * @author ParadiseWY
 * @date 2020-12-05 22:55:02
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
@EnableCaching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}