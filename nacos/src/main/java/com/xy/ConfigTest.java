
package com.xy;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigChangeEvent;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.AbstractConfigChangeListener;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConfigTest {

    //private final static String NACOS_SERVER_ADDRESS = "http://10.10.220.46:8848";
    private final static String NACOS_SERVER_ADDRESS = "http://110.42.205.63:30916";

    @Test
    public void testPullConfig() throws Exception {
        try {
            // 设置Nacos服务器地址
            Properties properties = new Properties();
            properties.put("serverAddr", NACOS_SERVER_ADDRESS);
            // 创建ConfigService实例
            ConfigService configService = NacosFactory.createConfigService(properties);
            // 从Nacos服务器拉取指定的配置
            String javaVersion = configService.getConfig("application.yml", "DEFAULT_GROUP", 50000);
            System.out.println(javaVersion);
        } catch (NacosException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testPublishConfig() throws Exception {
        // 设置Nacos服务器地址
        Properties properties = new Properties();
        properties.put("serverAddr", NACOS_SERVER_ADDRESS);
        properties.put("username","nacos");
        properties.put("password","Szu,1234");
        // 创建ConfigService实例
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 向Nacos服务器发布指定的配置
        boolean published = configService.publishConfig("xchaos", "dev", "name:yyf");
        System.out.println(published);
    }

    @Test
    public void testUpdateConfig() throws Exception {
        // 设置Nacos服务器地址
        Properties properties = new Properties();
        properties.put("serverAddr", NACOS_SERVER_ADDRESS);
        // 创建ConfigService实例
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 向Nacos服务器发布指定的配置
        boolean published = configService.publishConfig("xchaos", "dev", "name:yyf");
    }

    @Test
    public void testListenerConfig() throws Exception {
        // 设置Nacos服务器地址
        Properties properties = new Properties();
        properties.put("serverAddr", NACOS_SERVER_ADDRESS);
        // 创建ConfigService实例
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 添加监听器
        configService.addListener("xchaos", "dev", new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("监听到数据变更:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return Executors.newSingleThreadExecutor();
            }
        });

        configService.addListener("xchaos", "dev", new AbstractConfigChangeListener() {
            @Override
            public void receiveConfigChange(ConfigChangeEvent event) {

            }
        });

        System.in.read();

    }

}
