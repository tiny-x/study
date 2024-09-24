package com.xy.simple;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Product {

    public static void main(String[] args) throws Exception {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.228.220:32508,10.10.228.213:31827,10.10.224.90:31459");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer(prop, new StringSerializer(), new StringSerializer());

        while (true) {
            kafkaProducer.send(new ProducerRecord<>("test_xy", "str")
                    , new Callback() {
                        @Override
                        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                            System.out.println(recordMetadata);
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });
            TimeUnit.SECONDS.sleep(3);
        }

    }
}
