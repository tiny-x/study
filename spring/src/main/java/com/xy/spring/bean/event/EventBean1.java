package com.xy.spring.bean.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class EventBean1 implements ApplicationListener<EventBean1.AEvent> {

    @Override
    public void onApplicationEvent(AEvent event) {
        System.out.println("AEvent: " + event.toString());
    }

    public static class AEvent extends ApplicationEvent{
        public AEvent(Object source) {
            super(source);
        }

        @Override
        public String toString() {
            return "a event;";
        }
    }
}
