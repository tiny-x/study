package com.xy.concurrent.future;

import java.util.concurrent.*;

/**
 * @author yefei
 * @date 2017-06-29 17:18
 */
public class FutureExample {

    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws Exception {

        Future<String> future = executorService.submit( () -> {
                TimeUnit.SECONDS.sleep(2);
                return "hello world!";
        });
        String result = future.get();
        System.out.println(result);

        FutureTask<Integer> futureTask = new FutureTask<>(() -> 200);

        executorService.submit(futureTask);
        System.out.println(futureTask.get());

        System.out.println(future.get());
        executorService.shutdown();
    }
}
