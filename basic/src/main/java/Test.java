import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.currentTimeMillis();
            int add = add(1, 10);
            System.out.println("test-1, 耗时：" + (System.currentTimeMillis() - l));
        }
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
