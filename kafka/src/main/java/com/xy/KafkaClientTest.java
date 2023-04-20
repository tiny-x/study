package com.xy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class KafkaClientTest {

    KafkaConsumer kafkaConsumer;

    private static Map<String, Object> genConsumerConfig(String brokerIp, String brokerPort, String group) {
        Map<String, Object> prc = new HashMap<>();
        prc.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerIp + ":" + brokerPort);
        prc.put("group.id", group);
        prc.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        prc.put("auto.commit.interval.ms", "1000");
        return prc;
    }

    private void kafkaClient() {
        String topic = "test";
        kafkaConsumer = new KafkaConsumer<String, String>(genConsumerConfig("10.10.224.25", "9092", "test"), new StringDeserializer(), new StringDeserializer());
        kafkaConsumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords poll = kafkaConsumer.poll(100);
            System.out.println(poll.count());
        }
    }

    public static void main(String[] args) throws Exception {
        KafkaClient kafkaClient = new KafkaClient();
        kafkaClient.init();
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            kafkaClient.doRun();
        }
    }
}
