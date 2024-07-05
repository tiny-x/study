package com.xy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class JedisTest {

    private static final Logger logger = LoggerFactory.getLogger(JedisTest.class);

    public static void main(String[] args) {
        //创建一个连接
        Jedis jedis = new Jedis("10.10.224.19", 31284);
        //ping一下Redis服务端是否在线，成功则返回 “PONG” 反之报错超时
        String ping = jedis.ping();
        //Jedis它实现了各式各样接口，最终汇聚一个类Jedis，它内部封装了全部Redis命令
        //比如下面的”String类型命令set“保存两条数据，保存成功则返回ok
        jedis.set("name", "zhangsan");
        jedis.set("age", "22");
        //在redis中获取name值

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
                long start = System.currentTimeMillis();
                String name = jedis.get("name");
                System.out.println(name);
                logger.info("key: [name]; value: [{}], time: [{}ms]", name, (System.currentTimeMillis() - start));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }


    }
}
