package com.xy;

import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class MongodbTest {

    public static void main(String[] args) throws Exception {

        MongodbClient mongodbClient = new MongodbClient();
        mongodbClient.getConnection();
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            long l = System.currentTimeMillis();
            mongodbClient.mockMongodb();
            System.out.printf("耗时 %d \n", System.currentTimeMillis() - l);
        }
    }

}
