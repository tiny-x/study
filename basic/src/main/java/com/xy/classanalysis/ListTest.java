package com.xy.classanalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * @date 2018-05-24 10:33
 */
public class ListTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");

        for (String s : list) {
            System.out.println(s);
        }
        list.addAll(list);

        for (String s : list) {
            System.out.println(s);
        }
    }
}
