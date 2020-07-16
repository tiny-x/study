package com.xy.dubbo.benchmark.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GenericConsumer {

    private static GenericService helloService;

    static {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("yyy");
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setGeneric(true);
        reference.setApplication(application);
        reference.setInterface("com.xy.dubbo.demo.HelloService");
        reference.setVersion("1.0.0");
        reference.setUrl("dubbo://127.0.0.1:20880");
        reference.setTimeout(4000);
        MethodConfig methodConfig = new MethodConfig();
        methodConfig.setName("sayHello");
        methodConfig.setTimeout(2000);
        ArrayList<MethodConfig> methodConfigs = new ArrayList<>();
        methodConfigs.add(methodConfig);

        reference.setMethods(methodConfigs);
        helloService = reference.get();

    }

    public static void main(String[] args) throws Exception {
        GenericConsumer consumer = new GenericConsumer();

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            try {


                long x = System.currentTimeMillis();
                String s = consumer.invoke();
                System.out.println("耗时： " + (System.currentTimeMillis() - x) + "ms \tresult: " + s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 5, TimeUnit.SECONDS);

    }

    public String invoke() {
        return (String) helloService.$invoke("sayHello", new String[] {"java.lang.String"}, new Object[] {"sf"});
    }
}
