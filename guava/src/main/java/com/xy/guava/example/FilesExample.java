package com.xy.guava.example;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;

public class FilesExample {

    public static void main(String[] args) throws Exception {
        CharSource charSource = Files.asCharSource(new File("/Users/yefei/Documents/tmp/2.log"), Charsets.UTF_8);
        ImmutableList<String> strings = charSource.readLines();
        for (String string : strings) {
            System.out.println(string);
        }

    }
}
