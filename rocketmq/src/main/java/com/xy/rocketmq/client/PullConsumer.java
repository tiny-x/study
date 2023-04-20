package com.xy.rocketmq.client;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PullConsumer {

    private static final Map<MessageQueue, Long> OFFSE_TABLE = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws Exception {
        DefaultMQPullConsumer defaultMQPullConsumer = new DefaultMQPullConsumer("PullConsumer3");
        defaultMQPullConsumer.setNamesrvAddr("10.10.228.213:9876");
        defaultMQPullConsumer.start();

        Set<MessageQueue> mqs = defaultMQPullConsumer.fetchSubscribeMessageQueues("TopicTest");


        while (true) {
            TimeUnit.SECONDS.sleep(2);

            for (MessageQueue mq : mqs) {
                //System.out.printf("Consume from the queue: %s%n", mq);
                SINGLE_MQ:
                while (true) {
                    try {
                        PullResult pullResult =
                                defaultMQPullConsumer.pull(mq, "*6", getMessageQueueOffset(mq), 32);
                        System.out.printf("%s%n", pullResult);
                        putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                        switch (pullResult.getPullStatus()) {
                            case FOUND:
                                List<MessageExt> msgFoundList = pullResult.getMsgFoundList();
                                for (MessageExt messageExt : msgFoundList) {
                                    System.out.println(new String(messageExt.getBody()));
                                }
                                break;
                            case NO_MATCHED_MSG:
                                break;
                            case NO_NEW_MSG:
                                break SINGLE_MQ;
                            case OFFSET_ILLEGAL:
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
      //  defaultMQPullConsumer.shutdown();
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSE_TABLE.get(mq);
        if (offset != null)
            return offset;

        return 0;
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        OFFSE_TABLE.put(mq, offset);
    }
}
