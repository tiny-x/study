package com.xy.spring.bean.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *  bean 生命周期
 */
public class BeanLifeCycle implements BeanNameAware,
         ApplicationContextAware, InitializingBean, DisposableBean {

    private String desc;

    public void init() {
        System.out.println("xml 配置 init");
    }

    public void destroyed() {
        System.out.println("xml 配置 destroy");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("执行 setBeanName");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy bean");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        desc = "ha ha ha";
        System.out.println("InitializingBean afterPropertiesSet");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    public String getDesc() {
        return desc;
    }
}
