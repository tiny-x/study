package com.xy.spring.bean.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanNameAdvisorCreate {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        User user = context.getBean(User.class);
        String name = user.getName();
        System.out.println(name);
    }

    static class User {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Configuration
    static class Config {

        @Bean(name = "user")
        public User user() {
            return new User();
        }

        @Bean(name = "logAdvisor")
        public Advisor advisor() {
            return new DefaultPointcutAdvisor(new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    System.out.println("日志记录");
                    return invocation.proceed();
                }
            });
        }

        @Bean
        public BeanNameAutoProxyCreator creator() {

            BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
            creator.setBeanNames("user");
            creator.setInterceptorNames("logAdvisor");
            return creator;
        }
    }
}
