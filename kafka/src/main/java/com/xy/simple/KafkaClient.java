package com.xy.simple;

import cn.hutool.core.date.DateUtil;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class KafkaClient {

    public static void main(String[] args) throws Exception {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.228.220:32508,10.10.228.213:31827,10.10.224.90:31459");

        //kafkaConsumer.subscribe(Arrays.asList("chaos_plugin_metrics"));
        prop.put("group.id", "test-poll-delay");
        prop.put("max.poll.interval.ms", "1000");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(prop, new StringDeserializer(), new StringDeserializer());

        kafkaConsumer.subscribe(Arrays.asList("test_xy"));

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(5000);
            Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
            while (iterator.hasNext()) {
                // max.poll.interval.ms = 1000, 此处处理 1500 验证 Kafka将认为消费者已死亡，并将其标记为离线
                //TimeUnit.SECONDS.sleep(500);
                ConsumerRecord<String, String> next = iterator.next();
                Date date = new Date(next.timestamp());
                System.out.printf("当前时间 %s, 收到消息: %s ,\t 消息内容: %s \n", DateUtil.now(), DateUtil.date(date), next.value());
            }
        }
    }
}
