package com.xy;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ApacheHttpClient3GET {

    private static final Logger logger = LoggerFactory.getLogger(ApacheHttpClient3GET.class);

    public static void main(String args[]) throws Exception {
        //1.构造HttpClient的实例
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("utf-8");
        //设置连接超时：
        //设置读取超时：
        //httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

        //2.构造PostMethod的实例
        PostMethod postMethod = new PostMethod("https://www.baidu.com");
        //like12 add,20160511,中文转码 //在头文件中设置转码
        //postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
        postMethod.addRequestHeader("Content-Type", "application/json;charset=utf-8");

        try {
            while (true) {
                TimeUnit.SECONDS.sleep(3);
                long start = System.currentTimeMillis();
                // 4.执行postMethod,调用http接口
                httpClient.executeMethod(postMethod);//200

                //5.读取内容
                //String responseMsg = postMethod.getResponseBodyAsString().trim();
                //########################################################//
                //解决报警告的问题WARN : org.apache.commons.httpclient.HttpMethodBase -
                //Going to buffer response body of large or unknown size.
                //Using getResponseBodyAsStream instead is recommended.
                InputStream is = postMethod.getResponseBodyAsStream();
                //like12 modified,20220530,解决中文乱码的问题
                //BufferedReader br = new BufferedReader(new InputStreamReader(is));
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sb = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                String responseMsg = sb.toString();
                //########################################################//
                System.out.println("responseMsg:" + responseMsg);
                //6.处理返回的内容

                logger.info("key: [name]; time: [{}ms]", (System.currentTimeMillis() - start));

            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //7.释放连接
            postMethod.releaseConnection();
        }


    }
}
