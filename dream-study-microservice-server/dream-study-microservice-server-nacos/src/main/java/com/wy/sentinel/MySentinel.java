package com.wy.sentinel;

import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.authority.AuthoritySlot;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeSlot;
import com.alibaba.csp.sentinel.slots.block.flow.FlowSlot;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;
import com.alibaba.csp.sentinel.slots.statistic.StatisticSlot;
import com.alibaba.csp.sentinel.slots.system.SystemSlot;

/**
 * Sentinel限流,降级,作用和Hystrix类似
 * 
 * {@link SentinelResource}:定义限流,降级资源,提供可选的异常处理和fallback配置项
 * 
 * <pre>
 * {@link SentinelResource#value}:资源名称
 * {@link SentinelResource#entryType}:entry类型,标记流量的方向,取值IN/OUT,默认OUT
 * {@link SentinelResource#blockHandler()}:处理BlockException的函数名称,函数要求:
 * 		1. 必须是:public
 * 		2.返回类型:参数与原方法一致
 * 		3. 默认需和原方法在同一个类中,若希望使用其他类的函数,可配置blockHandlerClass,并指定blockHandlerClass里的方法
 * {@link SentinelResource#blockHandlerClass()}:存放blockHandler的类,对应的处理函数必须static修饰
 * {@link SentinelResource#fallback()}:用于在抛出异常的时候提供fallback处理逻辑,可以针对所有类型的异常
 * 		(除了exceptionsToIgnore 里面排除掉的异常类型)进行处理,函数要求:
 * 		1. 返回类型与原方法一致
 * 		2. 参数类型需要和原方法相匹配
 * 		3. 默认需和原方法在同一个类中.若希望使用其他类的函数,可配置fallbackClass,并指定fallbackClass里面的方法
 * {@link SentinelResource#fallbackClass()}:存放fallback的类,对应的处理函数必须static修饰
 * {@link SentinelResource#defaultFallback()}:用于通用的 fallback 逻辑,默认fallback()可以针对所有类型的异常进行处理.
 * 		若同时配置了 fallback 和 defaultFallback,以fallback为准,方法要求:
 * 		1. 返回类型与原方法一致
 * 		2. 方法参数列表为空,或者有一个 Throwable 类型的参数
 * 		3. 默认需要和原方法在同一个类中.若希望使用其他类的函数,可配置fallbackClass,并指定 fallbackClass 里面的方法
 * {@link SentinelResource#exceptionsToIgnore()}:指定排除掉哪些异常.排除的异常不会计入异常统计,也不会进入fallback(),而是原样抛出
 * {@link SentinelResource#exceptionsToTrace()}:需要trace的异常
 * </pre>
 * 
 * {@link SentinelWebInterceptor}:实施请求拦截和保护
 * 
 * <pre>
 * {@link SentinelWebInterceptor}:拦截请求,基于责任链模式的Slot设计
 * ->{@link NodeSelectorSlot}:前置处理规则,和下面2个用于收集,统计,分析必须的数据.
 * 		主要负责收集资源路径并将这些资源调用路径,以树状结构存储起来,用于根据调用路径来限流降级
 * ->{@link ClusterBuilderSlot}:前置处理规则,用于收集,统计,分析必须的数据.
 * 		用于存储资源的统计信息以及调用者信息,例如该资源的RT(运行时间),QPS,thread count等,这些信息将用作为多维度限流,降级的依据
 * ->{@link StatisticSlot}:前置处理规则,用于收集,统计,分析必须的数据.
 * 		用于记录,统计不同维度的runtime 信息
 * ->{@link SystemSlot}:系统规则.通过系统的状态,例如CPU,内存的情况,来控制总的入口流量
 * ->{@link AuthoritySlot}:认证规则.根据黑白名单,来做黑白名单控制
 * ->{@link FlowSlot}:限流规则.则用于根据预设的限流规则,以及前面 slot 统计的状态,来进行限流
 * ->{@link DegradeSlot}:熔断规则.则通过统计信息,以及预设的规则,来做熔断降级
 * ->{@link RestController}:请求
 * </pre>
 * 
 * @author 飞花梦影
 * @date 2022-02-26 17:10:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class MySentinel {

}