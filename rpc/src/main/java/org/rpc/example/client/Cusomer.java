package org.rpc.example.client;

import org.rpc.example.api.HelloService;
import org.rpc.rpc.RpcClient;
import org.rpc.rpc.RpcProxyFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The type Cusomer.
 * 消费者测试
 *
 * @author yefei
 * @date 2017 -06-20 17:07
 */
public class Cusomer {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring-customer.xml");

        HelloService service = RpcProxyFactory.factory(HelloService.class) //
                .client(context.getBean(RpcClient.class)) //
                .newProxy(); //
        for (int i = 0; i < 1000; i++) {
            String str = service.sayHello(" Cusomer " + i);
            System.out.println(str);
        }
    }
}
