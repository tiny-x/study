package com.xy.topic;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class KafkaNewTopic {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.224.90:9092");

        AdminClient adminClient = AdminClient.create(prop);

//        for (int i = 0; i < 10; i++) {
//            NewTopic newTopic = new NewTopic("chaos_plugin_event" + i, 8, (short) 1);
//            ArrayList<NewTopic> newTopics = new ArrayList<>();
//            newTopics.add(newTopic);
//            CreateTopicsResult topics = adminClient.createTopics(newTopics);
//        }


        ListTopicsResult listTopicsResult = adminClient.listTopics();
        Collection<TopicListing> topicListings = listTopicsResult.listings().get();
        System.out.println(topicListings);

        TimeUnit.SECONDS.sleep(3);
    }
}
