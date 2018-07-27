package com.xy.spring.bean.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class AEventListener implements ApplicationListener<AEventListener.AEvent> {

    @Override
    public void onApplicationEvent(AEvent event) {
        System.out.println("Listeneing Evnet: " + event.toString());
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
