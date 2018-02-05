package com.xy.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureJoin {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture future1 = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture future2 = CompletableFuture.supplyAsync(() -> "world");

        CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);
        System.out.println("执行完成: " + future1.get() + future2.get());

        future.handle((response, throwable) -> {
            return "";
        });
    }
}
