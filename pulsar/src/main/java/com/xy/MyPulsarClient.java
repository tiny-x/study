package com.xy;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static org.apache.pulsar.client.api.SubscriptionType.Shared;

@Slf4j
public class MyPulsarClient {

    private Long consumerCostTime = 50L;

    private boolean consumerSleep = false;

    private String pulsarUrl = "pulsar://10.10.228.211:6650";
    PulsarClient client;

    String topicName = "chaos";
    String subscriptionName = "order";
    Consumer consumer;
    Producer<String> stringProducer;

    public void getClient() {
        try {
            client = PulsarClient.builder()
                    .serviceUrl(pulsarUrl)
                    .build();
            consumer = client.newConsumer()
                    .topic(topicName)
                    //抢占模式消费
                    .subscriptionType(Shared)
                    .subscriptionName(subscriptionName)
                    .subscribe();
            stringProducer = client.newProducer(Schema.STRING).topic(topicName)
                    .create();

        } catch (Exception e) {
            log.error("初始化失败：", e);
        }

    }

    /**
     * org.apache.pulsar.client.impl.ProducerBase
     * <p>
     * send(Msg)  this.internalSendAsync(message);
     * sendAsync(Msg)  this.internalSendAsync(message);
     * <p>
     * send(T)  this.newMessage().value(message).send();
     * sendAsync(T)  this.newMessage().value(message).sendAsync();
     *
     * @throws PulsarClientException
     */
    public void produceSomething() throws PulsarClientException {
        String msg = "pulsar发送信息；发送时间戳:" + System.currentTimeMillis();
        stringProducer.send(msg);
    }


    /**
     * org.apache.pulsar.client.impl.ConsumerBase
     * receive()-->internalReceive() -->this.incomingMessages.take();
     * receive(int timeout, TimeUnit unit) -->internalReceive(timeout, unit); --> this.incomingMessages.poll((long)timeout, unit);
     * receiveAsync()-->internalReceiveAsync() -->this.incomingMessages.poll();
     *
     * @throws PulsarClientException
     */
    public void consumeSomething() throws PulsarClientException {
        // Wait for a message
        Message msg = consumer.receive(100, TimeUnit.MILLISECONDS);
        if (msg == null) {
            return;
        }
        try {
            // Do something with the message
            String msgData = new String(msg.getData());
            long timeCost = TimeCostUtil.execAndSleepTimeCost(msgData, consumerCostTime, consumerSleep);
            log.info("pulsar消费数据: " + msgData + ";耗费时间：" + timeCost);
            // Acknowledge the message so that it can be deleted by the message broker
            consumer.acknowledge(msg);
        } catch (Exception e) {
            // Message failed to process, redeliver later
            consumer.negativeAcknowledge(msg);
        }

    }


    public static void main(String[] args) throws Exception {
        MyPulsarClient myPulsarClient = new MyPulsarClient();
        myPulsarClient.getClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        myPulsarClient.produceSomething();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        myPulsarClient.consumeSomething();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        System.in.read();
    }

}
