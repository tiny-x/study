package com.xy.order;

import java.util.Arrays;

public class BubbleSort {

    /**
     * 每次把最大的挪到最后面，第一次 交换 判断 n-1次， 第二次 n-2
     * 时间复杂度 n^2 , 稳定排序
     */
    public static void main(String[] args) {
        Integer[] array = new Integer[]{3, 2, 1, 6, 9, 0};
        int temp = 0;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j + 1] < array[j]) {
                    temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                }
            }
        }

        System.out.println(Arrays.toString(array));
    }
}
