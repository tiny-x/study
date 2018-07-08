package com.logback;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileReader {

    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("/Users/yefei/Documents/tmp/2.log");

        InputStreamReader in = new InputStreamReader(fileInputStream);

        StringBuffer stringBuffer = new StringBuffer();
        for (int temp = 0; ; temp = in.read()) {
            char a = (char) temp;
            stringBuffer.append(a);
            if (a == '\n') {
                System.out.print(stringBuffer.toString());
                stringBuffer.setLength(0);
            }
        }
    }
}
