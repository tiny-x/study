package com;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @author xf.yefei
 */
public class DynamicCompile {

    public static void main(String[] args) {
        JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
    }
}
