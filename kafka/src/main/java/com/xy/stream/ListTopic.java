package com.xy.stream;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ListTopic {

    public static void main(String[] args)  throws Exception {
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
        Collection<String> deletes = new ArrayList<>();
        for (TopicListing topicListing : topicListings) {
            if (topicListing.name().startsWith("data-agg")) {
                deletes.add(topicListing.name());
            }
        }
        adminClient.deleteTopics(deletes);

        System.out.println(topicListings);
    }
}
