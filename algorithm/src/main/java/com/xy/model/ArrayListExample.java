package com.xy.model;

import java.util.ArrayList;
import java.util.List;

public class ArrayListExample {

    public static void main(String[] args) {

        List<User> list = new ArrayList<>();
        list.add(new User(1));
        list.add(new User(2));
        list.add(new User(3));

        /**
         *  elementData[--size] = null; // clear to let GC do its work
         *  目测当删除最后一个元素，才会使gc roots链不可达。
         */
        list.remove(2);

        /**
         * 根据 equals 判断。
         */
        list.remove(new User(2));

        System.out.println(list);

    }

    static class User {
        int age;

        public User(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            return age == user.age;
        }

        @Override
        public int hashCode() {
            return age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "age=" + age +
                    '}';
        }
    }
}
