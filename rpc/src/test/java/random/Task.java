package random;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Task implements Runnable {

    private static Random random = ThreadLocalRandom.current();
    // private static Random random = new Random();
               
    @Override
    public void run() {
        System.out.println(random.nextInt());
    }
    
    public static void main(String[] args) {
        ExecutorService taskPool = Executors.newCachedThreadPool();
        for(int n = 1; n < 10; n++) {
            taskPool.submit(new Task());
        }
        taskPool.shutdown();
    }

}