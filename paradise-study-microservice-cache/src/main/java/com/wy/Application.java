package com.wy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * Cache:缓存使用
 * 
 * Java定义了2个核心接口:<br>
 * {@link CacheManager}:缓存管理器,管理各种Cache组件,统一不同的缓存技术,支持注解开发
 * {@link Cache}:缓存接口,定义缓存操作,常见实现有RedisCache,EhCacheCache,ConcurrentMapCache等
 * 每次调用需要缓存功能的方法时,spring会检查指定参数的指定目标方法是否已经被调用过:
 * 如果有就直接从缓存中获取方法调用后的结果;如果没有则调用方法并缓存结果到缓存中后将结果返回前端
 * 
 * {@link EnableCaching}:若需要使用缓存,需要开启该注解,将缓存纳入到spring上下文中
 * 
 * @apiNote{@link CacheConfig}:类上注解,指定类中通用属性.若方法上的属性和类上的属性重复,使用方法上的属性值
 * 
 * @apiNote {@link Cacheable}:该注解的value/cacheNames类似于命名空间,若不写,则默认为类的完整路径名+方法名
 *          key():缓存的key值,可用SpEl表达式,不可用#result作为key,会有异常冲突,默认是所有参数的形参值,可用的el表达式:
 *          methodName:当前被调用的方法名,如#root.methodName,可直接为#methodName,root是方法参数的名称;
 *          method:当前被调用的方法,和methodName类似,#root.method;<br>
 *          target:被调用的目标对象,#root.target;<br>
 *          targetClass->被调用的目标对象类,#root.targetClass;<br>
 *          args->被调用的方法参数列表,#root.args;<br>
 *          argname->被调用方法的参数名,可以是#参数名或#p0,等,0代表索引;<br>
 *          result->返回的结果集,不可使用#result做key<br>
 *          keyGenerator():key的生成器,和key属性只能使用一个,需要实现{@link KeyGenerator}接口<br>
 *          condition():符合条件下才缓存<br>
 *          unless():否定缓存,和condition相反,符合条件就不缓存.可以获取结果进行判断<br>
 * 
 * @apiNote {@link CachePut}:该注解表示既调用方法,又更新缓存,基本用在更新操作.更新缓存的时候需要注意,
 *          是否和查询使用的key一样,若不一样,即使更新了也没用
 * 
 * @apiNote {@link CacheEvict}:该注解清除缓存数据.和cacheput一样,也需要注意key的值要和查询的一样,
 *          否则查询相同的key时,仍可以从缓存中取得数据.默认是传参的值<br>
 *          allEntries():清除value/cachenames中所有的缓存,默认是false<br>
 *          beforeInvocation():是否在方法执行完之前清除缓存,默认false,之后清除
 * 
 * @apiNote {@link Caching}:该注解在复杂情况下使用,可配置多种情况下的缓存操作
 *
 * @author ParadiseWY
 * @date 2020-12-05 22:55:02
 * @git {@link https://github.com/mygodness100}
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}