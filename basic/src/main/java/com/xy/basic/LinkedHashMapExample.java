package com.xy.basic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yefei
 * @date 2018-05-30 16:22
 */
public class LinkedHashMapExample {

    public static void main(String[] args) {

        LinkedHashMap<String, String> map = new LinkedHashMap<>(5, 0.75F,true);
        map.put("a","a");
        map.put("b","a");
        map.put("c","a");
        map.put("d","a");

        map.get("a");

        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            System.out.println(stringStringEntry.getKey());
        }
    }
}
