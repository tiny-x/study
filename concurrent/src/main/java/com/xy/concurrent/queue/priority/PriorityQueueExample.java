package com.xy.concurrent.queue.priority;

import java.util.PriorityQueue;

/**
 * @author yefei
 * @date 2018-01-09 15:46
 *
 *
 * PriorityQueue是基于优先堆的一个无界队列，
 * 这个优先队列中的元素可以默认自然排序或者通过提供的Comparator（比较器）在队列实例化的时排序。
 *
 */
public class PriorityQueueExample {

    public static void main(String[] args) {

        final PriorityQueue<User> users = new PriorityQueue<>();

        users.add(new User(9, "A"));
        users.add(new User(7, "A"));
        users.add(new User(5, "A"));

        while(users.size() > 0) {
            System.out.println(users.poll());
        }

    }
}
