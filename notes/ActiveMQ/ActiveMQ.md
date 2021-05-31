# ActiveMQ



# 安装

* apache官网下载,解压
* 主要的配置文件有active的jar包,
* data:ActiveMQ数据默认存储文件夹
* webapps:ActiveMQ网页端监控,配置文件都在conf文件夹中
* bin:该文件夹中有个active脚本,可用来start,stop,status当前active
* conf:该文件中有active的各种配置文件
  * jetty.xml:启动webapps需要配置,类似tomcat的settings.xml
  * jetty-realm.properties:登录网页端配置的用户名和密码等
  * groups,user.properties:Jetty的权限配置文件
  * active.xml:ActiveMQ主要配置文件,集群,端口等在该文件配置



# 组件



## Destination

* 相比于RabbitMQ多了一个Destination,由JMS Provider(消息中间件)负责维护,用于管理Message.而Producer需要指定Destination才能发送消息,Consumer也需要指定Destination才能接收消息



## Producer

* 消息的生产者,发送message到目的地,应用接口为MessageProducer



## Consumer(Receiver)

* 消息消费者,负责从目的地中(处理,监听,订阅)message,应用接口MessageConsumer



## Message

* 消息内容.常见有StreamMessage,BytesMessage,TextMessage,ObjectMessage,MapMessage.若是要实现自己的消息接口,实体类就需要实现Serializable接口



## ConnectionFactory

* 连接工厂,非jdbc的工厂



## Connection

* 链接,创建访问ActiveMQ连接,由工厂创建



## Session

* 会话,一次持久有效有状态的访问,由链接创建



## Queue

* 队列,是Destination的子接口,处在队列中的消息,只能由一个Consumer消费,消费完之后删除



## Topic

* 主题,Destination的子接口,和RabbitMQ中的Topic差不多,可重复处理信息

<!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->
<!-- START SNIPPET: example -->
<beans
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd">

    <!-- Allows us to use system properties as variables in this configuration file -->
    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="locations">
            <value>file:${activemq.conf}/credentials.properties</value>
        </property>
    </bean>
    
    <!-- Allows log searching in hawtio console -->
    <bean id="logQuery" class="org.fusesource.insight.log.log4j.Log4jLogQuery"
          lazy-init="false" scope="singleton"
          init-method="start" destroy-method="stop">
    </bean>
    
    <!--
        The <broker> element is used to configure the ActiveMQ broker.
    -->
    <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}">
    
        <!-- 安全验证 -->
        <!-- 配置jaas验证插件,登录的用户名密码等在login.conf里配置 -->
         <!--
         <plugins>
            <jaasAuthenticationPlugin configuration="activemq" />
            <authorizationPlugin>
                <map>
                    <authorizationMap>
                        <authorizationEntries>
                            topic:表示topic模式,>表示所有的topic请求都验证.admins是用户组,在login.conf中配置
                            <authorizationEntry topic=">" read="admins" write="admins" admin="admins" />
                            <authorizationEntry queue=">" read="admins" write="admins" admin="admins" />
                            active内部自用
                            <authorizationEntry topic="ActiveMQ.Advisory.>" read="admins" write="admins" admin="admins"/>
                            <authorizationEntry queue="ActiveMQ.Advisory.>" read="admins" write="admins" admin="admins"/>
                        </authorizationEntries>
                    </authorizationMap>
                </map>
            </authorizationPlugin>
        </plugins>
    	-->
        <destinationPolicy>
            <policyMap>
              <policyEntries>
                <policyEntry topic=">" >
                    <!-- The constantPendingMessageLimitStrategy is used to prevent
                         slow topic consumers to block producers and affect other consumers
                         by limiting the number of messages that are retained
                         For more information, see:
    
                         http://activemq.apache.org/slow-consumer-handling.html
    
                    -->
                  <pendingMessageLimitStrategy>
                    <constantPendingMessageLimitStrategy limit="1000"/>
                  </pendingMessageLimitStrategy>
                </policyEntry>
              </policyEntries>
            </policyMap>
        </destinationPolicy>


        <!--
            The managementContext is used to configure how ActiveMQ is exposed in
            JMX. By default, ActiveMQ uses the MBean server that is started by
            the JVM. For more information, see:
    
            http://activemq.apache.org/jmx.html
        -->
        <managementContext>
            <managementContext createConnector="false"/>
        </managementContext>
       
        <!--
            Configure message persistence for the broker. The default persistence
            mechanism is the KahaDB store (identified by the kahaDB tag).
            For more information, see:
    
            http://activemq.apache.org/persistence.html
        -->
        <!-- 持久化策略 -->
        <!-- kahadb:一个文件型数据库,使用内存加文件保证数据持久化 -->
        <!-- kahadb是以日志形式存储消息,类似redis持久化的AOF.消息索引是B-Tree结构,支持JMS事务,支持多种恢复机制 -->
        <persistenceAdapter>
        		 <!-- directory:持久化数据目录,journalMaxFileLength:限制每个文件的大小,不是总大小 -->
            <kahaDB directory="${activemq.data}/kahadb" journalMaxFileLength="16mb"/>
        </persistenceAdapter>
        
        <!-- amq 
        <persistenceAdapter>
            <amqPersistenceAdapter directory="${activemq.data}/amq" maxFileLength="32mb"/>
        </persistenceAdapter>
        -->
    	<!-- JDBC:数据库持久化,datasource数据源,需要配置一个bean,bean需要写在broker外.若数据库驱动用其他类,需要将jar包复制到active的lib文件夹中.createTablesOnStartup是否在启动mq时创建表,第一次启动时可选true,第2此启动之后就可以改为false 
        <persistenceAdapter>
            <jdbcPersistenceAdapter dataSource="#mysql-ds" createTablesOnStartup="false"/>
        </persistenceAdapter>
    	-->
    	
          <!--
            The systemUsage controls the maximum amount of space the broker will
            use before disabling caching and/or slowing down producers. For more information, see:
            http://activemq.apache.org/producer-flow-control.html
          -->
          <systemUsage>
            <systemUsage>
                <memoryUsage>
                    <memoryUsage percentOfJvmHeap="70" />
                </memoryUsage>
                <storeUsage>
                    <storeUsage limit="100 gb"/>
                </storeUsage>
                <tempUsage>
                    <tempUsage limit="50 gb"/>
                </tempUsage>
            </systemUsage>
        </systemUsage>
    
        <!--
            The transport connectors expose ActiveMQ over a given protocol to
            clients and other brokers. For more information, see:
    
            http://activemq.apache.org/configuring-transports.html
        -->
        <transportConnectors>
            <!-- DOS protection, limit concurrent connections to 1000 and frame size to 100MB -->
            <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="stomp" uri="stomp://0.0.0.0:61613?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="ws" uri="ws://0.0.0.0:61614?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        </transportConnectors>
    
        <!-- destroy the spring context on shutdown to stop jetty -->
        <shutdownHooks>
            <bean xmlns="http://www.springframework.org/schema/beans" class="org.apache.activemq.hooks.SpringContextHook" />
        </shutdownHooks>
    
    </broker>
    
    <!-- JDBC Datasource configuration 
    <bean id="mysql-ds" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost/activemq"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
        <property name="maxActive" value="200"/>
        <property name="poolPreparedStatements" value="true"/>
    </bean>
    -->
    
    <!--
        Enable web consoles, REST and Ajax APIs and demos
        The web consoles requires by default login, you can disable this in the jetty.xml file
    
        Take a look at ${ACTIVEMQ_HOME}/conf/jetty.xml for more details
    -->
    <import resource="jetty.xml"/>

</beans>
<!-- END SNIPPET: example -->