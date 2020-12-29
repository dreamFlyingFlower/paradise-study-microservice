# Kafka

* kafka由多个broker组成,每个broker是一个节点
* 创建一个topic,这个topic可以划分为多个partition,每个partition可以存在于不同的broker上,每个partition就放一部分数据
* 这就是天然的分布式消息队列,一个topic的数据,是分散放在多个机器上的,每个机器就放一部分数据
* kafka提供了HA机制,就是replica副本机制
* 每个partition的数据都会同步到其他机器上,形成自己的多个replica副本
* 然后所有replica会选举一个leader出来,那么生产和消费都跟这个leader打交道,然后其他replica就是follower
* 写数据的时候,leader会负责把数据同步到所有follower上去,读的时候就直接读leader上数据即可
* 写数据时,生产者就写leader,其他follower主动从leader来pull数据,一旦所有follower同步好数据了,就会发送ack给leader,leader收到所有follower的ack之后,就会返回写成功的消息给生产者
* kafka会均匀的将一个partition的所有replica分布在不同的机器上,这样才可以提高容错性



# 消息重复消费

* 比如A服务消费了MQ中的消息,A刚要回复MQ时挂了,而MQ没有等到A的回复,那MQ就认为该消息还没被消费
* 当A服务重启的时候,发现上次消费了的消息还在,继续消费,此时就发生了重复消费
* 解决的办法是没有的,只能减少,比如每次消费前从Redis中查询该消息是否被消费,没有就继续消费,有就跳过.但该方法只是换汤不换药,若是在A服务向Redis中写消息的时候挂了,一样会出现重复消费



# 消息丢失

## 1RabbitMQ

* 生产者使用confirm机制
* MQ对数据持久化
* 消费者需要手动进行ACK机制确认



## 2 Kafka



# 顺序消费

* 将需要进行顺序消费的数据都放在一个queue中,而不是放在多个queue中



# 数据积压

* 临时增加queue数量