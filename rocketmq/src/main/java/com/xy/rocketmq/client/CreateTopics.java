package com.xy.rocketmq.client;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

import static com.xy.rocketmq.client.Producer.getAclRPCHook;

public class CreateTopics {

    public static void main(String[] args) throws Exception {

        //需要一个producer group名字作为构造方法的参数，这里为producer1
        DefaultMQProducer producer = new DefaultMQProducer("producer1", getAclRPCHook());

        //设置NameServer地址,此处应改为实际NameServer地址，多个地址之间用；分隔
        //NameServer的地址必须有，但是也可以通过环境变量的方式设置，不一定非得写死在代码里
        producer.setNamesrvAddr("10.10.220.46:9876");
        producer.setVipChannelEnabled(false);
        // producer.setRetryTimesWhenSendFailed(20);
        // producer.setRetryTimesWhenSendAsyncFailed(20);
        //调用start()方法启动一个producer实例
        producer.start();

        for (int i = 0; i < 10; i++) {
            producer.createTopic("", "test_" + i, 1);
        }

    }

}
