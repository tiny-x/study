package com.xy;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.junit.Test;

public class ServiceTest {

    private final static String NACOS_SERVER_ADDRESS = "http://10.10.220.46:8848/";

    @Test
    public void register() throws Exception {
        Instance instance = new Instance();
        instance.setIp("10.10.220.46");//IP
        instance.setPort(3306);//端口
        instance.setServiceName("CartService");//服务名
        instance.setEnabled(true);//true: 上线 false: 下线
        instance.setHealthy(true);//健康状态
        instance.setWeight(1.0);//权重
        instance.addMetadata("nacos-sdk-java-discovery", "true");//元数据
        NamingService namingService = NamingFactory.createNamingService(NACOS_SERVER_ADDRESS);
        namingService.registerInstance("CartService", instance);
        System.in.read();
    }

    @Test
    public void subscribe() throws Exception {
        NamingService namingService = NamingFactory.createNamingService(NACOS_SERVER_ADDRESS);
        namingService.subscribe("CartService", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println("订阅服务");
                if (event instanceof NamingEvent) {
                    System.out.println(((NamingEvent) event).getServiceName());
                    System.out.println(((NamingEvent) event).getGroupName());
                    System.out.println(((NamingEvent) event).getInstances());
                }
            }
        });
    }
}
