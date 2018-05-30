package com.xy.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * @date 2018-04-18 14:38
 */
public class GenericExample {

    public static void main(String[] args) {
        List<Object> objects = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        List<?> xx = new ArrayList<>();
        /**
         * 原生态类型, 逃避了泛型检查
         */
        List array = new ArrayList<>();
        array = objects;
        array = strings;
        array = xx;

        /**
         * 参数化类型，安全检查
         *
         * 参数化类型 是 原生态类型的子类型
         * ListTest<String> 不是 ListTest<Object>的子类型
         */
        //objects = strings;
        xx = objects;
        xx = strings;
        // 除了null 不可放入
        xx.add(null);

        List<? extends Object> yy = new ArrayList<>();
        Object o = yy.get(0);
    }
}
