package com.xy.stream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {

    static String kafkaServer = "10.10.222.155:9092,10.10.30.83:9092,10.10.30.84:9092\n";

    static String topic = "chaos_plugin_event";

    static Properties properties;


    public static void main(String[] args) throws Exception {

        StreamsBuilder register = register(kafkaServer, topic, "test-stream", Serdes.String(), 2, 2, buildConfig());

        KStream<String, String> stringStringKStream = kStream(register);

        KafkaStreams streams = new KafkaStreams(register.build(), properties);
        streams.start();
        System.in.read();
    }

    public static KStream<String, String> kStream(StreamsBuilder builder) {
        //1. 定义数据来源
        KStream<String, String> kStream = builder.<String, String>stream(topic);
        //2. 定义数据处理流程
        kStream
                //2.1 对原始数据中的value字符串进行切割   mapValues : 对流中数据的value进行处理转化
                .mapValues(value -> value.split(" "))
                //2.2 对value数组进行扁平化处理(将多维数组转化为一维数组)   flatMapValues : 对流中数据的数组格式的value进行处理转化(多维转一维)
                .flatMapValues(value -> Arrays.asList(value))
                //2.3 对数据格式进行转化, 使用value作为key   map : 对流中数据的key和value进行处理转化
                .map(((key, value) -> new KeyValue<>(value, value)))
                //2.4 对key进行分组  groupByKey : 根据key进行分组
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                //设置聚合时间窗口, 在指定时间窗口范围之内的数据会进行一次运算, 输出运算结果
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                //2.5 求每一个组中的单词数量   count : 组内计算元素数量
                .count(Materialized.with(Serdes.String(), Serdes.Long()))
                //2.6 将运算结果发送到另一个topic中   toStream : 将其他类型的流转化为 kStream
                .toStream()
                .map((key, value) -> new KeyValue<>(key.key(), value.toString()))
                //将运算结果发送到一个topic, 供消费者接收
                .to("chaos_plugin_event_result");

        //3. 返回KStream对象
        return kStream;
    }

    private static Properties buildConfig() {
        Properties properties = new Properties();
        //每条消息的最大大小
        properties.put("max.request.size", "10485760");
        //每次拉取消息数
        properties.put("max.poll.records", "2000");
        // 此配置定义了消费者处理单个调用poll()方法的最大时间。
        // 如果在此时间内未调用poll()，则Kafka将认为消费者已死亡，并将其标记为离线。
        // 这个配置可用于控制消费者处理消息的速度。默认值为5分钟。
       // properties.put("max.poll.interval.ms", "6000");
        // 该配置定义了消费者向服务器发出请求的超时时间。
        // 如果在此时间内未收到服务器的响应，消费者将认为请求失败并尝试重新发送请求。默认值为30秒。
     //   properties.put("request.timeout.ms", "30000");
        // 该配置定义了消费者与Kafka集群之间的会话超时时间。
        // 如果消费者在此超时时间内未发送心跳到服务器，
        // 服务器将将其标记为离线并触发重新平衡操作。默认值为10秒。
       // properties.put("session.timeout.ms", "10000");
        return properties;
    }

    public static StreamsBuilder register(String serverAddress, String topic, String applicationId, Serde serde, int streamThreads, int factor, Properties config) {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        properties = new Properties();
        //配置kafka连接端口
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        //配置消息只消费一次（默认最少一次)
        //properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_BETA);
        //配置数据默认序列化方式方式
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, serde.getClass());
        //应用id配置 topic前缀
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        //最大副本配置
        //properties.put(StreamsConfig.MAX_WARMUP_REPLICAS_CONFIG, 1);
        //复制因子配置
        properties.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, factor);
        //拉取消息的最大大小
        properties.put("max.request.size", "10485760");
        //第一次启动流从哪里开始获取数据，此处设置为从最新的消息开始消费
        properties.put("auto.offset.reset", "latest");
        properties.put("message.max.bytes", "10485760");
        //设置state存储地址
//        properties.put(StreamsConfig.STATE_DIR_CONFIG, "/data/kafka-stream");
        //并行计算线程数
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, streamThreads);
        //properties.put(StreamsConfig.BUFFERED_RECORDS_PER_PARTITION_CONFIG, 5);
        //默认向下游推送数据时间，默认30秒，此处设置1秒时为了数据尽快向下游推送
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        //自动关闭空闲连接时长
        //properties.put(StreamsConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, -1);
        //rocksDB配置
        properties.putAll(config);

        //配置用于时间窗口的时间提取器
//        properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, StreamTimestampExtractor.class.getName());
//        properties.put("replica.fetch.max.bytes", "10485760");
//        properties.put("fetch.message.max.bytes", "10485760");
        return streamsBuilder;
    }
}
