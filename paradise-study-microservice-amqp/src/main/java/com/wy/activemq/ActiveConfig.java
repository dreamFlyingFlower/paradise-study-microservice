package com.wy.activemq;

/**
 * @description ActiveMQ2种模式的比较,开启安全认证,见docs/ActiveMQ.pdf
 * @description 持久化:消息默认是保存在内存中,若内存不足或正常关闭时,会将未处理的消息保存到磁盘中.
 *              具体的持久化方式由配置文件决定.默认的存储策略是kahadb,若使用JDBC作为策略,
 *              则会将所有需要持久化的数据保存到数据库中. 所有的持久化配置都在activemq.xml的broker中.
 * @instruction kahadb:默认持久化策略,是以日志存储消息,消息索引是B-Tree结构,支持JMS事务,支持多种恢复机制
 * @instruction JDBC:数据库持久方式,不限制数据库类型,但是数据库所需的jar包,驱动等都必须自己添加到lib文件夹.
 *              在启动activemq的时候根据配置是否新建表,若已经新建过,则应该将配置改为不新建.表会有3个,详细见文档
 * @instruction 在发送消息时,有一种可指定消息过期时间的方法,若是消息过期,且持久化,则消息会存到死信队列中(DLQ)
 *              若是非持久化策略,超时则会直接消失.死信队列中的数据不能恢复
 * @instruction 优先级,默认是不开启的,也无法严格按照优先级处理,见文档
 * @instruction 消息确认:当Producer和Consumer都开启事务,且消息确认都是CLIENT_ACKNOWLEDGE时,
 *              那么当高并发的时候,点对点模式下,即使有多个消费者,也不会出现重复消费消息的情况,
 *              但若是Consumer不确认消息已经被消费,那么这些没有被确认的消息会存在持久化数据中.
 *              当原先的所有消费者都关闭时,并重新开启一个消费者时,他会重复消费那些没有确认的消息.
 *              必须是原先已经开启的消费者都关闭时再开启才会出现这种情况,因为原来的消费者仍然会将消息持有
 * @instruction ActiveMQ主从配置见文档,ActiveMQ集群就是多个ActiveMQ主从,但是配置仍需要改,见文档
 * @author ParadiseWy
 * @date 2019年5月20日 下午8:56:31
 * @git {@link https://github.com/mygodness100}
 */
public class ActiveConfig {

}