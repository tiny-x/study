import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-05-31 11:03
 */
public class NumberAdd {

    /**
     * -javaagent:D:\workspace\idea\study\instrument\target\instrument-1.0.0.jar
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        NumberAdd numberAdd = new NumberAdd();
        while (true) {
            Random random = new Random();
            int a = random.nextInt(10);
            int b = random.nextInt(10);
            int add = numberAdd.add(a, b);
            System.out.println("a(" + a + ")\t+\tb(" + b + ")\t=" + add);
            TimeUnit.SECONDS.sleep(3);
        }
    }

    public int add(int a, int b) {
        return a + b;
    }
}
