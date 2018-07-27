package com.xy.spring.bean.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * bean 生命周期
 */
public class BeanLifeCycle implements BeanNameAware,
        ApplicationContextAware, InitializingBean, DisposableBean, Lifecycle {

    private String desc;

    @Override
    public void setBeanName(String name) {
        System.out.println("BeanNameAware 执行 setBeanName: " + name);
    }

    @Override
    public void start() {
        System.out.println("Lifecycle start");
    }

    @Override
    public void stop() {
        System.out.println("Lifecycle stop");
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @PostConstruct
    public void init() {
        System.out.println("jsr250 初始化方法");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        desc = "ha ha ha";
        System.out.println("InitializingBean afterPropertiesSet");
    }

    public void initXML() {
        System.out.println("xml 配置 init");
    }

    @PreDestroy
    public void destroyed250() {
        System.out.println("jsr250 destroy");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy bean");
    }

    public void destroyed() {
        System.out.println("xml 配置 destroy");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "BeanLifeCycle{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
