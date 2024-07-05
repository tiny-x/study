package com.xy.test;

import cn.hutool.core.date.DateUtil;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

/**
 * @author xf.yefei
 */
public class KafkaClient {

    public static void main(String[] args) throws Exception {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.220.46:9092");

        prop.put("security.protocol", "SASL_PLAINTEXT");
        prop.put("sasl.mechanism", "PLAIN");
        prop.put("client.id", "otel-collector-metrics");
        prop.put("group.id", "otel-collector");
        prop.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"123456\";");

        KafkaConsumer kafkaConsumer = new KafkaConsumer<String, String>(prop, new StringDeserializer(), new StringDeserializer());
        //kafkaConsumer.subscribe(Arrays.asList("chaos_plugin_metrics"));
        kafkaConsumer.subscribe(Arrays.asList("test111"));

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(5000);
            Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
            while (iterator.hasNext()) {
                ConsumerRecord<String, String> next = iterator.next();
                Date date = new Date(next.timestamp());
                System.out.printf("当前时间 %s, 收到消息: %s ,\t 消息内容: %s \n", DateUtil.now(), DateUtil.date(date), next.value());
            }
        }
    }
}
