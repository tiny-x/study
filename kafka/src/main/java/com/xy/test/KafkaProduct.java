package com.xy.test;

import cn.hutool.core.date.DateUtil;
import com.xy.AbstractClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class KafkaProduct extends AbstractClient {

    private String kafkaip = "10.10.220.46";

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
        pr.put("security.protocol", "SASL_PLAINTEXT");
        pr.put("sasl.mechanism", "PLAIN");
        pr.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"kafka\" password=\"ca$hc0wA\";");
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
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(5000);
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
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "10.10.220.46:9092");

        prop.put("security.protocol", "SASL_PLAINTEXT");
        prop.put("sasl.mechanism", "PLAIN");
        prop.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"123456\";");


        AdminClient adminClient = AdminClient.create(prop);
        NewTopic newTopic = new NewTopic("test111", 8, (short) 1);
        ArrayList<NewTopic> newTopics = new ArrayList<>();
        newTopics.add(newTopic);

        //CreateTopicsResult topics = adminClient.createTopics(newTopics);
        //System.out.println(topics.all().get());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(prop, new StringSerializer(), new StringSerializer());
        while (true) {
            long start = System.currentTimeMillis();
            try {
                String value = "kafka消息,发送时间戳:" + DateUtil.now();
                System.out.printf("发送消息 %s \t", value);
                ProducerRecord record = new ProducerRecord("test111", value);
                Callback callback = new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        long elapse = System.currentTimeMillis() - start;
                    }
                };
                producer.send(record, callback);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                long elapse = System.currentTimeMillis() - start;
                System.out.printf("发送耗时 %d \n", elapse);
            }
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
