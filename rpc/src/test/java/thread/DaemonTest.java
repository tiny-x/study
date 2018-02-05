/**
 *
 */
package thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leaf 2017年6月23日下午2:01:45
 */
public class DaemonTest {

    static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

        AtomicInteger count = new AtomicInteger();

        ThreadGroup threadGroup = new ThreadGroup("DaemonTest");

        public Thread newThread(Runnable r) {

            Thread thread = new Thread(threadGroup, r, "------Daemon Thred------" + count.incrementAndGet());
            // thread.setDaemon(true);
            return thread;
        }
    });

    public static void main(String[] args) {
        service.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getThreadGroup().getName() + "：hello world");
            }
        }, 1, 2, TimeUnit.SECONDS);
    }

}
