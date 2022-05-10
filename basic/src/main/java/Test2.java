import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class Test2 {

    public static void main(String[] args) throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.currentTimeMillis();
            int add = add(1, 10);
            System.out.println("test-2, 耗时：" + (System.currentTimeMillis() - l));
        }
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
