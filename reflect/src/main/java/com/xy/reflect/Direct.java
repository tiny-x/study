package com.xy.reflect;

public class Direct {

    private final HelloService helloService = new HelloService();

    public void invoke() {
        helloService.hello("Direct");
    }
}
