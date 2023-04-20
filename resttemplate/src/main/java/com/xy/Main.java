package com.xy;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xf.yefei
 */
public class Main {
    public static void main(String[] args) {
        RestTemplate template = new RestTemplate();
        String url = "https://www.baidu.com?dt={dt}&ht={ht}";

// 封装参数，这里是HashMap
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dt", "20190225");
        paramMap.put("ht", "10");

//1、使用getForObject请求接口
        String result1 = template.getForObject(url, String.class, paramMap);
        System.out.println("result1====================" + result1);

//2、使用exchange请求接口
        HttpHeaders headers = new HttpHeaders();
        headers.set("id", "lidy");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(null, headers);
        ResponseEntity<String> response2 = template.exchange(url, HttpMethod.GET, httpEntity, String.class, paramMap);
    }
}
