package com.xy.concurrent.queue.delay;

import java.util.Calendar;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-01-09 11:31
 */
public class User implements Delayed {

    private int age;

    private String name;

    // 延迟到什么时间
    private long time;

    public User(int age, String name) {
        this.age = age;
        this.name = name;
        // 延迟3s
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, 3);
        this.time = instance.getTimeInMillis();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        // 毫秒 --> 纳秒
        return unit.convert(time - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 指定元素的顺序
     * 队列使用PriorityQueue来实现
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
