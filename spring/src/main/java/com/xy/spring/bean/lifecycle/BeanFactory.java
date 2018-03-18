package com.xy.spring.bean.lifecycle;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class BeanFactory {

    public static void main(String[] args) throws IOException {
        XmlBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("spring.xml"));

        BeanLifeCycle beanLifeCycle = (BeanLifeCycle) beanFactory.getBean("cycle2");
        System.out.println(beanLifeCycle.getDesc());

        System.in.read();

    }
}
