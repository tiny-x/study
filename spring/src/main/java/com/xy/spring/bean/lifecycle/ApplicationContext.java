package com.xy.spring.bean.lifecycle;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 相比 bean factory
 * 支持国际化配置
 * 启动检查依赖
 * 自动注册bean后置处理器
 */
public class ApplicationContext {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        BeanLifeCycle beanLifeCycle = (BeanLifeCycle) context.getBean("cycle2");
        System.out.println(beanLifeCycle.getDesc());

        context.destroy();
        System.in.read();
    }
}
