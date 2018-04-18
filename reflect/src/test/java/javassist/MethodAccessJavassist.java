package javassist;

import com.xy.reflect.HelloService;
import org.junit.Test;

public class MethodAccessJavassist {

    @Test
    public void main() {
        HelloService helloService = new HelloService();

        com.xy.reflect.method.access.MethodAccessJavassist methodAccessJavassist = com.xy.reflect.method.access.MethodAccessJavassist.get(HelloService.class);

        Object cc = methodAccessJavassist.invoke(helloService, "hello", "cc");
        System.out.println(cc);

        methodAccessJavassist.invoke(helloService, "hi", "biu biu");
    }
}
