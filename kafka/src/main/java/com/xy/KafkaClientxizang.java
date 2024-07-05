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
public class KafkaClientxizang {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.125.10.52:32085,10.125.128.110:32102,10.125.129.88:32019");

        prop.put("security.protocol", "SASL_PLAINTEXT");
        prop.put("sasl.mechanism", "SCRAM-SHA-256");
        prop.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username = \"chaos-kafka\" password = \"Perfma888!\";");

        AdminClient adminClient = AdminClient.create(prop);

        NewTopic newTopic = new NewTopic("xchaos.kafka.chaos_plugin_topic", 8, (short) 1);
        ArrayList<NewTopic> newTopics = new ArrayList<>();
        newTopics.add(newTopic);

        CreateTopicsResult topics = adminClient.createTopics(newTopics);
        System.out.println(topics.all().get());
    }
}
