package com.xy.reflect;

public class ReflectJavassist {

    public static void main(String[] args) throws Exception {
        HelloService helloService = new HelloService();

        MethodAccess methodAccess = MethodAccess.get(HelloService.class);

        methodAccess.invoke(helloService, 0, "cc");
    }

}
