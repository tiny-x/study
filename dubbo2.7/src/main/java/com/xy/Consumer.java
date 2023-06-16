package com.xy;

import com.example.demo.service.HelloService;
import org.apache.dubbo.config.*;

import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class Consumer {

    public static void main(String[] args) {


        // 当前应用配置
        ApplicationConfig applicationc = new ApplicationConfig();
        applicationc.setName("yyy");

        // 引用远程服务
        ReferenceConfig<HelloService> reference = new ReferenceConfig<HelloService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(applicationc);
        reference.setInterface(HelloService.class);
        reference.setVersion("1.0.0");
        reference.setUrl("dubbo://127.0.0.1:30000");
       // reference.setRegistry(new RegistryConfig("zookeeper://10.10.225.128:2181"));
        reference.setTimeout(4000);
        // 和本地bean一样使用xxxService
        HelloService helloService = reference.get();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 20000; i++) {
            try {
                TimeUnit.SECONDS.sleep(2);
                long start = System.currentTimeMillis();
                String s = helloService.sayHello(" biu biu biu!!!");
                System.out.println("耗时：" + (System.currentTimeMillis() - start) + s);
                //System.out.printf("---------->: receive provider message %s \n", s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
