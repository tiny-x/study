package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author xf.yefei
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    @Async
    public String sayHello(String a) {
        log.info("sayHello");
        return a;
    }

    @Async(value = "sayHello2tp")
    @Override
    public void sayHello2() {
        log.info("sayHello2");
    }
}
