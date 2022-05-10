import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class TestImpl implements Test {

    public static void main(String[] args) throws Exception {
        Test t = new TestImpl();
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.currentTimeMillis();
            int add = t.add(1, 10);
            System.out.println("test-1, 耗时：" + (System.currentTimeMillis() - l));
        }
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
