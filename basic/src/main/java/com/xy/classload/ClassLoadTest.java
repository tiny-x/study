package com.xy.classload;

import java.io.IOException;
import java.io.InputStream;

public class ClassLoadTest {

    public static void main(String[] args) throws Exception {
        Class<?> aClass = new ClassLoad1().loadClass("com.xy.classload.ClassLoadTest");
        Object o = aClass.newInstance();
        System.out.println(o instanceof ClassLoadTest);
        System.out.println(Thread.currentThread().getContextClassLoader().getClass());
    }

    static class ClassLoad1 extends ClassLoader {

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            String fileName =  name.substring(name.lastIndexOf(".") + 1) + ".class";

            InputStream inputStream = getClass().getResourceAsStream(fileName);
            if (inputStream == null) {
                return super.loadClass(name);
            }

            try {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }
}
