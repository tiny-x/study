package com.xy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class KafkaClient2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.228.220:32508,10.10.228.213:31827,10.10.224.90:31459");

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
        kafkaProducer.send(new ProducerRecord<>("exporter_metric_topic", "{\"exporterName\":\"node\",\"metrics\":[{\"name\":\"node_cpu_physical_size\",\"value\":4.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"node_cpu_logical_size\",\"value\":4.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"node_load1\",\"value\":1.11,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"node_load5\",\"value\":0.51,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"node_load15\",\"value\":0.31,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"node_mem_total\",\"value\":8.200613888E9,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_whole_cpu_usage_percent\",\"value\":0.2808253648716658,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"max_whole_cpu_usage_percent\",\"value\":0.2808253648716658,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"min_whole_cpu_usage_percent\",\"value\":0.2808253648716658,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_wait_cpu_usage_percent\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_mem_usage_percent\",\"value\":0.620886637700458,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"max_mem_usage_percent\",\"value\":0.620886637700458,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"min_mem_usage_percent\",\"value\":0.620886637700458,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"mem_total\",\"value\":8.200613888E9,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"mem_available\",\"value\":3.108962304E9,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"virtual_memory_total\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"virtual_memory_used\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_net_receive_bytes_rate\",\"value\":3743.8,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_net_send_bytes_rate\",\"value\":346643.4,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"tcp_v4_stats_connections_established\",\"value\":10.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"tcp_v4_stats_connections_active\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"tcp_v4_stats_connections_passive\",\"value\":1534.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_disk_usage_percent\",\"value\":0.61,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_disk_read_bytes_rate\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_disk_write_bytes_rate\",\"value\":1843.2,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_disk_writes\",\"value\":0.4,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_disk_reads\",\"value\":4380.549999999998,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"delay_time\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"context_switch\",\"value\":23908.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"open_file_descriptors\",\"value\":3328.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"max_file_descriptors\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"disk_io_time_proportion\",\"value\":[{\"diskName\":\"sr0\",\"ioProportion\":\"0.0\"},{\"diskName\":\"sda\",\"ioProportion\":\"0.0\"},{\"diskName\":\"sda1\",\"ioProportion\":\"0.0\"}],\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"disk_write_spend_time\",\"value\":[{\"diskName\":\"sr0\",\"writeSpendTime\":\"0.0\"},{\"diskName\":\"sda\",\"writeSpendTime\":\"6.5\"},{\"diskName\":\"sda1\",\"writeSpendTime\":\"6.5\"}],\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\"}},{\"name\":\"avg_disk_read_bytes_rate_per_disk\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\",\"disk_name\":\"/dev/sda\"}},{\"name\":\"avg_disk_write_bytes_rate_per_disk\",\"value\":1843.2,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\",\"disk_name\":\"/dev/sda\"}},{\"name\":\"avg_disk_writes_per_disk\",\"value\":0.4,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\",\"disk_name\":\"/dev/sda\"}},{\"name\":\"avg_disk_reads_per_disk\",\"value\":0.0,\"labels\":{\"reportIds\":\"[\\\"798312896764313600\\\"]\",\"disk_name\":\"/dev/sda\"}}],\"timestamp\":1726063935067,\"labels\":{\"hostName\":\"xchaos-test-1.perfma-inc.com\",\"instance\":\"10.10.220.145@37d2f56e\",\"instance_ip\":\"10.10.220.145\",\"job\":\"test\",\"relInstance\":\"10.10.220.145@37d2f56e\"}}")
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
