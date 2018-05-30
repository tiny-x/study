package com.xy.classanalysis;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yefei
 * @date 2018-05-24 10:38
 */
public class SetTest {

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");

        for (String s : set) {
            System.out.println(s);
        }
    }
}
