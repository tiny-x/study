import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Test {

    private static Logger logger = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) throws Exception {
        LogManager logManager = LogManager.getLogManager();
        Enumeration<String> loggerNames = logManager.getLoggerNames();
        FileHandler fileHandler = new FileHandler("/tmp/a.log");
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleFormatter());
        while (loggerNames.hasMoreElements()) {
            String x = loggerNames.nextElement();
            System.out.println(x);
            Logger logger1 = logManager.getLogger(x);
            logger1.addHandler(fileHandler);
        }
        System.out.println(System.getProperty("user.home"));

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.currentTimeMillis();
            int add = add(1, 10);
            logger.info(String.format("used: %d, %d ", memoryMXBean.getHeapMemoryUsage().getMax() / 1024 / 1024, memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024));
            System.out.println("test-1, 耗时：" + (System.currentTimeMillis() - l));
        }
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
