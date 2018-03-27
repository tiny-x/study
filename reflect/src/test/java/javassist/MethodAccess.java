package javassist;

import com.xy.reflect.HelloService;
import org.junit.Test;

public class MethodAccess {

    @Test
    public void main() {
        HelloService helloService = new HelloService();

        com.xy.reflect.method.access.MethodAccess methodAccess = com.xy.reflect.method.access.MethodAccess.get(HelloService.class);

        Object cc = methodAccess.invoke(helloService, "hello", "cc");
        System.out.println(cc);

        methodAccess.invoke(helloService, "hi", "biu biu");
    }
}
