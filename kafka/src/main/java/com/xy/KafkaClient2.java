package com.xy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Slf4j
public class KafkaClient2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.242.33.134:39099");

        prop.put("security.protocol", "SASL_PLAINTEXT");
        prop.put("sasl.mechanism", "PLAIN");
        prop.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"kafka\" password=\"cahc0wA\";");

        AdminClient adminClient = AdminClient.create(prop);

        NewTopic newTopic = new NewTopic("chaos_plugin_event", 8, (short) 1);
        ArrayList<NewTopic> newTopics = new ArrayList<>();
        newTopics.add(newTopic);

        CreateTopicsResult topics = adminClient.createTopics(newTopics);
        System.out.println(topics.all().get());
    }
}
