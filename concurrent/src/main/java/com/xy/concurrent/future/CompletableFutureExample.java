package com.xy.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture future = new CompletableFuture();
        new Thread(() -> {
            future.complete("hello");
        }).start();
        System.out.println(future.get());

        System.out.println(CompletableFuture.supplyAsync(() -> "hello").get());
    }
}
