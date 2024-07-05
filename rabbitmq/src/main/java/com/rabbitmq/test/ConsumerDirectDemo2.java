package com.rabbitmq.test;

import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 接收默认消息
 *
 * @author
 * @date 2019/01/10 11:14:32
 */
public class ConsumerDirectDemo2 {
    public static void main(String[] args) {
        String queneName = "Gnsscscectc01";
        Connection connection = null;
        Channel channel = null;
        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("47.94.58.99");
            factory.setPort(5672);
            factory.setUsername("cscectc01");
            factory.setPassword("Q5gRGR==N@dQ");
            factory.setVirtualHost("/");
            connection = factory.newConnection();
            channel = connection.createChannel();

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(envelope.getExchange() + "," + envelope.getRoutingKey() + "," + DateUtil.date().toString("yyyy-MM-dd HH:mm:ss") + " --- "+ message);
                }
            };
            // channel绑定队列，autoAck为true表示一旦收到消息则自动回复确认消息
            channel.basicConsume(queneName, true, consumer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
