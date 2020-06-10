package com.xy.dubbo.benchmark.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.xy.dubbo.demo.HelloService;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Consumer {

    private static HelloService helloService;

    static {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("yyy");
        ReferenceConfig<HelloService> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setInterface(HelloService.class);
        reference.setVersion("1.0.0");
        reference.setUrl("dubbo://127.0.0.1:20880");
        reference.setTimeout(4000);
        helloService = reference.get();

    }

    public static void main(String[] args) throws Exception {
        Consumer consumer = new Consumer();

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            long x = System.currentTimeMillis();
            String s = consumer.invoke();
            System.out.println("耗时： " + (System.currentTimeMillis() - x) + "ms \tresult: " + s);
        }, 1, 5, TimeUnit.SECONDS);

    }

    public String invoke() {
        return helloService.sayHello(" biu biu biu!!!");
    }
}
