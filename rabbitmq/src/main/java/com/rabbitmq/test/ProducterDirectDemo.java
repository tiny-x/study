package com.rabbitmq.test;

import java.io.IOException;
import java.text.DateFormat;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *
 * 默认发送，直接将消息发送到某个队列，默认交换机type为direct
 *
 * @author
 * @date 2019/01/10 11:17:10
 */
public class ProducterDirectDemo {
    public static void main(String[] args) throws IOException, TimeoutException {

        String queneName = "order21";
        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("10.10.220.46");
            factory.setPort(5672);
            factory.setUsername("admin");
            factory.setPassword("admin123456");
            factory.setVirtualHost("my_vhost");
            // 创建与RabbitMQ服务器的TCP连接
            connection = factory.newConnection();
            // 创建一个频道
            channel = connection.createChannel();
            // 声明默认的队列
            channel.queueDeclare(queneName, true, false, false, null);
            channel.queueDeclare(queneName+ "_shadow", true, false, false, null);
            while (true) {
                long l = System.currentTimeMillis();
                UUID uuid = UUID.randomUUID();
                byte[] bytes = uuid.toString().getBytes();
                channel.basicPublish("", queneName, null, bytes);
                channel.basicPublish("", queneName+ "_shadow", null, bytes);
                System.out.printf("耗时 %d %s %s\n ", System.currentTimeMillis() - l, DateUtil.date().toString("yyyy-MM-dd HH:mm:ss"), uuid.toString());
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
