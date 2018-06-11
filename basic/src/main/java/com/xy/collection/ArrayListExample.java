package com.xy.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author yefei
 * @date 2018-05-31 9:25
 */
public class ArrayListExample {

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            System.out.println(next);
            // list.add("4"); fast-fail
        }

        List<String> list1 = list.subList(1, 2);


    }
}
