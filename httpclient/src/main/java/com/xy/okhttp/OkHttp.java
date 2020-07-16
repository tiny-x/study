package com.xy.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class OkHttp {

    private static Logger logger = LoggerFactory.getLogger(OkHttp.class);

    public static void main(String[] args) throws Exception {
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .build();
        OkHttpClient client = new OkHttpClient.Builder().build();

        try {
            while(true) {
                TimeUnit.SECONDS.sleep(2);
                long l = System.currentTimeMillis();
                Response response = client.newCall(request).execute();
                logger.info("http code : {}, msg : {}", response.code(), new String(response.body().bytes(), StandardCharsets.UTF_8));
                System.out.println("耗时: " + (System.currentTimeMillis() - l) + "ms");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
