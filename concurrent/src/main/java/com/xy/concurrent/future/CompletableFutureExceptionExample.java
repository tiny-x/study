package com.xy.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExceptionExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture future = new CompletableFuture();
        new Thread(() -> {

            try {
                int i = 1/0;
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }).start();

        Object o = future.get();
        System.out.println(o);
    }
}
