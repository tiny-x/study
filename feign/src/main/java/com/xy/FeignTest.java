package com.xy;

import feign.Feign;
import feign.codec.StringDecoder;

import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class FeignTest {
    public static void main(String[] args) throws Exception {
        MyFeignInterface github = Feign.builder()
                .decoder(new StringDecoder())
                .target(MyFeignInterface.class, "https://www.baidu.com");

        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long start = System.currentTimeMillis();
            String jsonResult = github.mockFeign();
            System.out.println(jsonResult);
            System.out.printf("耗时 %d \n", System.currentTimeMillis() - start);
        }
    }
}
