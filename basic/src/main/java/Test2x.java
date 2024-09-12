import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.lang.System;

/**
 * @author xf.yefei
 */
public class Test2x {

    private String a = "adasdsalkdkjaskdjsaldjasldjasdjalskd";

    private static Map<String, Object> map = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        Object o = new Object();
        map.put("a",o);
        Object o1 = map.remove("a");
        System.out.println(o1 == o);

         o1 = map.remove("a");
        System.out.println(o1 == o);

        Integer i = null2(2, 0);
        i.toString();
        TimeUnit.SECONDS.sleep(2);
        long l = System.currentTimeMillis();
        int add = add(1, 10);
        System.out.println("test-2, time: " + (System.currentTimeMillis() - l));
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static Integer null2(int a, int b) {
        if (a == 1) {
            return null;
        }
        return 0;
    }
}
