package com.xy.spring.bean.aop.annotation;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void addUser() {
        doAddUser();
//        UserService userService = (UserService)AopContext.currentProxy();
//        userService.doAddUser();
    }

    public void doAddUser() {
        System.out.println("do add user!");
    }
}
