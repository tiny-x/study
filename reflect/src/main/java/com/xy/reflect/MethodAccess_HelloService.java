package com.xy.reflect;

public class MethodAccess_HelloService extends MethodAccess {
    public MethodAccess_HelloService() {
    }

    public Object id(Object var1) {
        return var1;
    }

    public Object invoke(Object var1, int var2, Object... var3) {
        switch(var2) {
        case 0:
            return ((HelloService)var1).hello((String)var3[0]);
        default:
            throw new RuntimeException("method cant not found, index: " + var2);
        }
    }

    public static void main(String[] args) {
        MethodAccess_HelloService methodAccess_helloService = new MethodAccess_HelloService();
        Object aa = methodAccess_helloService.invoke(new HelloService(), 0, "aa");
        System.out.println(aa);
    }
}