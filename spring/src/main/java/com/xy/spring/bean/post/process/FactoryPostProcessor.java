package com.xy.spring.bean.post.process;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class FactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        // 可修改bean的定义
        System.out.println("userService bean factoryBeanName :" + beanDefinition.getFactoryBeanName());
        System.out.println("userService bean beanClassName :" + beanDefinition.getBeanClassName());
        System.out.println("userService bean factoryMethodName :" + beanDefinition.getFactoryMethodName());
        System.out.println("userService bean description :" + beanDefinition.getDescription());
        System.out.println("userService bean scope :" + beanDefinition.getScope());
    }
}
