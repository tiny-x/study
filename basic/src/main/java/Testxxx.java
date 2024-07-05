import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class Testxxx {

    private static Logger logger = Logger.getLogger(Testxxx.class.getName());

    static final Lock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(11111);

        String[] split = "1,   2,3,4  6".split(",");
        for (String s : split) {
            System.out.println(s);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // io线程阻塞式 接受连接
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();

                        System.out.println("client：" + socket.getRemoteSocketAddress().toString() + "接入連接。");
                        // 无法支撑过多的连接数
                        Socket finalSocket = socket;
                        new Thread(() -> {
                            try {
                                // io线程阻塞式等待读事件(bio无法拆分读事件和读操作)
                                InputStream inputStream = finalSocket.getInputStream();
                                // 设配器模式 装饰器模式 编解码
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                System.out.println("recive -->" + finalSocket.getRemoteSocketAddress().toString() + ":" + reader.readLine());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
        Thread.State state = thread.getState();
        System.out.println(state.name());
        thread.interrupt();
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(e.getCause());
            }
        });
        Thread.State state2 = thread.getState();
        System.out.println(state2.name());
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.out.println(stackTrace);


        String property = System.getProperty("user.home");
        System.out.println(property);

        FileOutputStream fos = new FileOutputStream(property + "/xchaos.log");
        PrintStream printStream = new PrintStream(fos);
        printStream.printf("xxxx");

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
            try {
                TimeUnit.SECONDS.sleep(2);
                long l = System.currentTimeMillis();
                int add = add(1, 10);
                logger.info(String.format("used: %d, %d ", memoryMXBean.getHeapMemoryUsage().getMax() / 1024 / 1024, memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024));
                System.out.println("test-1, 耗时：" + (System.currentTimeMillis() - l));
                System.out.println(add);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static int add(int a, int b) throws Exception{
        lock.lock();
        System.out.println("------");
        lock.unlock();
        return a + b;
    }
}
