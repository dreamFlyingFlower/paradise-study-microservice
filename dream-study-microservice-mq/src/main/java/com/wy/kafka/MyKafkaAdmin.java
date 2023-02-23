package com.wy.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.requests.CreateTopicsRequest;

/**
 * AdminClient API
 * 
 * <pre>
 * {@link AdminClient}:AdminClient客户端对象
 * {@link NewTopic}:创建Topic
 * {@link CreateTopicsRequest}:创建Topic的返回结果
 *	{@link ListTopicsResult}:查询Topic列表
 *	{@link ListTopicsOptions}:查询Topic列表及选项
 *	{@link DescribeTopicsResult}:查询Topics
 *	{@link DescribeTopicsOptions}:查询Topics配置项
 * </pre>
 *
 * @author 飞花梦影
 * @date 2022-07-23 11:01:38
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class MyKafkaAdmin {

	public final static String TOPIC_NAME = "dream-topic";

	public static void main(String[] args) throws Exception {
		// 创建Topic实例
		createTopic();
		// 删除Topic实例
		delTopics();
		// 获取Topic列表
		topicLists();
		// 描述Topic
		describeTopics();
		// 修改Config
		alterConfig();
		// 查询Config
		describeConfig();
		// 增加partition数量
		incrPartitions(2);
	}

	/**
	 * 设置AdminClient
	 */
	public static AdminClient adminClient() {
		Properties properties = new Properties();
		// 设置Kafka配置
		properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.150:9092");
		return AdminClient.create(properties);
	}

	/**
	 * 创建Topic实例
	 */
	public static void createTopic() {
		AdminClient adminClient = adminClient();
		// 副本因子,partition副本总数量.假设有3个partition,副本因子设置为3,则每个partition都有一个副本
		Short rs = 1;
		NewTopic newTopic = new NewTopic(TOPIC_NAME, 1, rs);
		CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
		System.out.println("CreateTopicsResult : " + topics);
	}

	/**
	 * 获取Topic列表
	 */
	public static void topicLists() throws Exception {
		AdminClient adminClient = adminClient();
		// 是否查看internal选项
		ListTopicsOptions options = new ListTopicsOptions();
		options.listInternal(true);
		// ListTopicsResult listTopicsResult = adminClient.listTopics();
		ListTopicsResult listTopicsResult = adminClient.listTopics(options);
		Set<String> names = listTopicsResult.names().get();
		Collection<TopicListing> topicListings = listTopicsResult.listings().get();
		KafkaFuture<Map<String, TopicListing>> mapKafkaFuture = listTopicsResult.namesToListings();
		System.out.println(mapKafkaFuture.get());
		// 打印names
		names.stream().forEach(System.out::println);
		// 打印topicListings
		topicListings.stream().forEach((topicList) -> {
			System.out.println(topicList);
		});
	}

	/**
	 * 增加partition数量
	 */
	public static void incrPartitions(int partitions) throws Exception {
		AdminClient adminClient = adminClient();
		Map<String, NewPartitions> partitionsMap = new HashMap<>();
		NewPartitions newPartitions = NewPartitions.increaseTo(partitions);
		partitionsMap.put(TOPIC_NAME, newPartitions);

		CreatePartitionsResult createPartitionsResult = adminClient.createPartitions(partitionsMap);
		createPartitionsResult.all().get();
	}

	/**
	 * 删除Topic
	 */
	public static void delTopics() throws Exception {
		AdminClient adminClient = adminClient();
		DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(TOPIC_NAME));
		deleteTopicsResult.all().get();
	}

	/**
	 * 修改Config信息
	 */
	public static void alterConfig() throws Exception {
		AdminClient adminClient = adminClient();
		// Map<ConfigResource,Config> configMaps = new HashMap<>();
		//
		// // 组织两个参数
		// ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC,
		// TOPIC_NAME);
		// Config config = new Config(Arrays.asList(new
		// ConfigEntry("preallocate","true")));
		// configMaps.put(configResource,config);
		// AlterConfigsResult alterConfigsResult = adminClient.alterConfigs(configMaps);

		/**
		 * 从 2.3以上的版本新修改的API
		 */
		Map<ConfigResource, Collection<AlterConfigOp>> configMaps = new HashMap<>();
		ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
		AlterConfigOp alterConfigOp =
				new AlterConfigOp(new ConfigEntry("preallocate", "false"), AlterConfigOp.OpType.SET);
		configMaps.put(configResource, Arrays.asList(alterConfigOp));

		AlterConfigsResult alterConfigsResult = adminClient.incrementalAlterConfigs(configMaps);
		alterConfigsResult.all().get();
	}

	/**
	 * 查看配置信息
	 */
	public static void describeConfig() throws Exception {
		AdminClient adminClient = adminClient();
		// ConfigResource configResource = new
		// ConfigResource(ConfigResource.Type.BROKER, TOPIC_NAME);

		ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
		DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
		Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
		configResourceConfigMap.entrySet().stream().forEach((entry) -> {
			System.out.println("configResource : " + entry.getKey() + " , Config : " + entry.getValue());
		});
	}

	/**
	 * 描述Topic
	 * 
	 * name : dream-topic , desc: (name=dream-topic, internal=false, partitions=
	 * (partition=0, leader=192.168.220.128:9092 (id: 0 rack: null),
	 * replicas=192.168.220.128:9092 (id: 0 rack: null), isr=192.168.220.128:9092
	 * (id: 0 rack: null)), authorizedOperations=[])
	 */
	@SuppressWarnings("deprecation")
	public static void describeTopics() throws Exception {
		AdminClient adminClient = adminClient();
		DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList(TOPIC_NAME));
		Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.all().get();
		Set<Map.Entry<String, TopicDescription>> entries = stringTopicDescriptionMap.entrySet();
		entries.stream().forEach((entry) -> {
			System.out.println("name ：" + entry.getKey() + " , desc: " + entry.getValue());
		});
	}
}