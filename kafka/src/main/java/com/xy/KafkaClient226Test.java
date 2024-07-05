package com.xy;

import cn.hutool.core.date.DateUtil;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

/**
 * @author xf.yefei
 */
public class KafkaClient226Test {

    public static void main(String[] args) throws Exception {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.30.103:9092");

        prop.put("client.id", "otel-collector-metrics");
        prop.put("group.id", "otel-collector");

        KafkaConsumer kafkaConsumer = new KafkaConsumer<String, String>(prop, new StringDeserializer(), new StringDeserializer());
        //kafkaConsumer.subscribe(Arrays.asList("chaos_plugin_metrics"));
        kafkaConsumer.subscribe(Arrays.asList("xsky_agent_discovery"));

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(5000);
            Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
            while (iterator.hasNext()) {
                ConsumerRecord<String, String> next = iterator.next();
                Date date = new Date(next.timestamp());

                System.out.println(DateUtil.date(date).toString());
                System.out.println("-----------");
                System.out.println(next.value());
            }
        }
    }
}
