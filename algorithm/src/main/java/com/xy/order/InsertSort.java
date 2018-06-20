package com.xy.order;

import java.util.Arrays;

public class InsertSort {

    /**
     * 插入排序
     * 把一个值插入到有序的数组中去，适合部分有序、数据小
     */
    public static void main(String[] args) {

        Integer[] array = new Integer[]{3, 2, 1, 6, 9, 0};

        // 第一次认为 3 有序，依次把后边的插入有序数组里边去
        for (int i = 1; i < array.length; i++) {
            // 待插入元素
            int temp = array[i];

            int j = i - 1;
            // 从有序数组尾部扫描
            for (; j >= 0 && temp < array[j]; j--) {
                array[j + 1] = array[j];
            }
            array[j + 1] = temp;
        }

        System.out.println(Arrays.toString(array));
    }
}
