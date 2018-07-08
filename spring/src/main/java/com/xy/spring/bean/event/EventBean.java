package com.xy.spring.bean.event;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class EventBean implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("ContextRefreshedEvent: " + event.getApplicationContext().getApplicationName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("put event");
        applicationContext.publishEvent(new EventBean1.AEvent(this));
    }
}
