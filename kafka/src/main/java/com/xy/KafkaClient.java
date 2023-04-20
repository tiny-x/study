package com.xy;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Slf4j
public class KafkaClient extends AbstractClient {

    private String kafkaip = "10.10.224.72";

    private String kafkaGroup = "dev";

    private String topic = "test";
    private String topicRepeat = "testrepeat";
    KafkaProducer<String, String> kafkaProducer;
    /**
     * kafak消费者不能多线程公用
     */
    KafkaConsumer<String, String> kafkaConsumer;


    public void init() {
        Map<String, Object> pr = genProducerConfig(kafkaip, "9092");
        kafkaConsumer = new KafkaConsumer<String, String>(genConsumerConfig(kafkaip, "9092"), new StringDeserializer(), new StringDeserializer());
        kafkaProducer = new KafkaProducer<String, String>(pr, new StringSerializer(), new StringSerializer());
        kafkaConsumer.subscribe(Arrays.asList(topic, topicRepeat));
    }

    private Map<String, Object> genProducerConfig(String brokerIp, String brokerPort) {
        Map<String, Object> pr = new HashMap<>();
        pr.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerIp + ":" + brokerPort);
        pr.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        return pr;
    }

    private Map<String, Object> genConsumerConfig(String brokerIp, String brokerPort) {
        Map<String, Object> prc = new HashMap<>();
        prc.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerIp + ":" + brokerPort);
        prc.put("group.id", kafkaGroup);
        prc.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        prc.put("auto.commit.interval.ms", "1000");
        return prc;
    }

    @Override
    public void doRun() {
        producerLoop();
        consumerLoop();
    }

    /**
     * kafka多线程消费异常，去掉同步代码
     * 把消费者放在order里面最好，为了不改动指标，因为只有2个客户端，先暂定2个消费者
     */
    private synchronized void consumerLoop() {
        long start = System.currentTimeMillis();
        try {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(50L));
            log.info("kafka group:{},size:{}", kafkaGroup, consumerRecords.count());
            Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
            while (iterator.hasNext()) {
                ConsumerRecord<String, String> next = iterator.next();
                if (next.topic().equals(topicRepeat)) {
                    System.out.printf(next.value(), topicRepeat, kafkaGroup);
                }
            }
        } finally {
            long elapse = System.currentTimeMillis() - start;
        }
    }

    public void producekafkaRepeat(int num, String mode) {
        if ("clear".equals(mode)) {

        }
        for (int i = 0; i < num; i++) {
            kafkaProducer.send(new ProducerRecord(topicRepeat, i + ""));
        }
    }

    private void producerLoop() {
        long start = System.currentTimeMillis();
        try {
            String value = "kafka消息,发送时间戳:" + System.currentTimeMillis();
            ProducerRecord record = new ProducerRecord(topic, value);
            Callback callback = new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    long elapse = System.currentTimeMillis() - start;
                }
            };
            kafkaProducer.send(record, callback);
        } catch (Exception e) {
            throw e;
        } finally {
            long elapse = System.currentTimeMillis() - start;
        }

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.224.72:9092");
        AdminClient adminClient = AdminClient.create(prop);
        ConfigResource topicResource = new ConfigResource(Type.TOPIC, "paas_app_action");
        DescribeConfigsResult result = adminClient.describeConfigs(CollUtil.newArrayList(topicResource));
        //获取原来的配置
        ConfigEntry configEntry = result.all().get().get(topicResource).get("cleanup.policy");
        System.out.println(configEntry.value());
        //更新配置
        Config updateTopicConfig = new Config(CollUtil.newArrayList(new ConfigEntry("delete.retention.ms", "86400001")));
        Map<ConfigResource, Config> updateConfig = new HashMap<>();
        updateConfig.put(topicResource, updateTopicConfig);
        System.out.println(1111);

    }
}
