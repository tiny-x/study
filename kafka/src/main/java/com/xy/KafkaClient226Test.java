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
        System.out.println(System.currentTimeMillis());
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.228.220:32508,10.10.228.213:31827,10.10.224.90:31459");

        prop.put("client.id", "otel-collector-metrics");
        prop.put("group.id", "perfma-xcrab-default");

        KafkaConsumer kafkaConsumer = new KafkaConsumer<String, String>(prop, new StringDeserializer(), new StringDeserializer());
        //kafkaConsumer.subscribe(Arrays.asList("chaos_plugin_metrics"));
        kafkaConsumer.subscribe(Arrays.asList("exporter_metric_topic"));

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(5000);
            Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
            while (iterator.hasNext()) {
                ConsumerRecord<String, String> next = iterator.next();
                Date date = new Date(next.timestamp());

                System.out.println(next.offset());
                System.out.println("-----------");
            }
        }
    }
}
