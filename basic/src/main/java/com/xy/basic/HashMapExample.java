package com.xy.basic;

import java.util.HashMap;

/**
 * @author yefei
 * @date 2018-02-01 13:52
 */
public class HashMapExample {

    public static void main(String[] args) throws InterruptedException {

        // factor = 0.75 threshold = (int)(1x0.75) = 3
        // size 3, 插入第四个元素扩容
        final HashMap<Key, Object> map = new HashMap<>(4, 0.75F);
        map.put(new Key(1), new Object());
        map.put(new Key(2), new Object());
        map.put(new Key(3), new Object());

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // resize
                map.put(new Key(4), new Object());
            }
        }, "thread1");
        thread1.start();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // resize
                map.put(new Key(5), new Object());
            }
        }, "thread2");
        thread2.start();
        thread1.join();
        thread2.join();

        for (Key key : map.keySet()) {
            System.out.println(key);
        }

    }

    static class Key{

        int key;

        public Key(int key) {
            this.key = key;
        }

        /**
         * equals 不同的对象
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key1 = (Key) o;

            return key == key1.key;
        }

        /**
         * @return
         */
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Key{");
            sb.append("key=").append(key);
            sb.append('}');
            return sb.toString();
        }

        // 相同的hashCode 生成链表
        @Override
        public int hashCode() {
            return 1;
        }
    }

}
