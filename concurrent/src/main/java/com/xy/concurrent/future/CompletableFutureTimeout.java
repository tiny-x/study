package com.xy.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletableFutureTimeout {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture future = new CompletableFuture();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                future.complete("hello");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println(future.get(1, TimeUnit.SECONDS));

        System.out.println(CompletableFuture.supplyAsync(() -> "hello").get());
    }
}
