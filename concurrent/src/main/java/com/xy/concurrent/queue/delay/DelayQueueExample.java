package com.xy.concurrent.queue.delay;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

/**
 * @author yefei
 * @date 2018-01-09 11:29
 *
 * 一个使用优先级队列实现的无界阻塞队列
 */
public class DelayQueueExample {

    public static void main(String[] args) throws Exception {
        DelayQueue<Delayed> delayeds = new DelayQueue<>();
        delayeds.add(new User(1, "A"));
        delayeds.add(new User(2, "B"));

        delayeds.take();
    }
}
