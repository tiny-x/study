package org.rpc.rpc.consumer.future;

import java.util.concurrent.CountDownLatch;

public class RpcFuture<V> {

    private V v;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void set(V v) {
        this.v = v;
        countDownLatch.countDown();
    }

    public V get() {
        try {
            countDownLatch.await();
        } catch (InterruptedException ignore) {

        }
        return v;
    }
}
