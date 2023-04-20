package com.xy;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.TimeUnit;

public class ApacheHttpClient4GET {

    public static void main(String args[]) throws Exception {


        // Setting the route planner to the HttpClientBuilder object
        HttpClientBuilder clientBuilder = HttpClients.custom();

        // Building a CloseableHttpClient
        CloseableHttpClient httpclient = clientBuilder.build();

        // Creating an HttpGet object
        HttpGet httpget = new HttpGet("https://www.baidu.com");

        long startTimerConnect = System.currentTimeMillis();

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            long start = System.currentTimeMillis();
            // Executing the Get request
            HttpResponse httpresponse = httpclient.execute(httpget);
            long stopTimerConnect = System.currentTimeMillis();
            int connect_time = (int) ((int) stopTimerConnect - startTimerConnect);
            System.out.println("ConnetionTime: " + connect_time);

            // Printing the status line
            System.out.println(httpresponse.getStatusLine());

            // Printing all the headers of the response
            Header[] headers = httpresponse.getAllHeaders();

            for (int i = 0; i < headers.length; i++) {
                System.out.println(headers[i]);
            }

            // Printing the body of the response
            HttpEntity entity = httpresponse.getEntity();

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
            System.out.printf(" ------ 耗时 %d", System.currentTimeMillis() - start);
        }
    }
}
