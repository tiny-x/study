package com.xy.spring.bean.event;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("listening ContextRefreshedEvent: " + event.getSource());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("put event: [AEvent]");
        applicationContext.publishEvent(new AEventListener.AEvent(this));
    }
}
