package com.xy;

import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.10.228.225:6379");

        RedissonClient redisson = Redisson.create(config);
        RList<Object> abc = redisson.getList("abc");
        for (; ; ) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.currentTimeMillis();
            abc.add("abc");
            System.out.printf("耗时 %d \n", System.currentTimeMillis() - l);
        }
    }
}
