package com.xy.stream;

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
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.222.155:9092,10.10.30.83:9092,10.10.30.84:9092");

//        prop.put("security.protocol", "SASL_PLAINTEXT");
//        prop.put("sasl.mechanism", "PLAIN");
//        prop.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user1\" password=\"jrZ4JsD089\";");

        AdminClient adminClient = AdminClient.create(prop);

//        NewTopic newTopic = new NewTopic("chaos_plugin_event", 8, (short) 1);
//        ArrayList<NewTopic> newTopics = new ArrayList<>();
//        newTopics.add(newTopic);
//
//        CreateTopicsResult topics = adminClient.createTopics(newTopics);
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        Collection<TopicListing> topicListings = listTopicsResult.listings().get();

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer(prop, new StringSerializer(), new StringSerializer());

        List<String> strs = new ArrayList<String>();
        strs.add("hello word");
        strs.add("hello kafka");
        strs.add("hello spring kafka");
        strs.add("kafka stream");
        strs.add("spring kafka");

        while (true) {
            for (String str : strs) {
                kafkaProducer.send(new ProducerRecord<>("chaos_plugin_event", str)
                        , new Callback() {
                            @Override
                            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                                System.out.println(recordMetadata);
                                if (e != null) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
