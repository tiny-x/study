package com.xy.basic;

public class ArrayClone {

    public static void main(String[] args) {
        User[] users = new User[1];
        User user = new User(9);
        users[0] = user;

        User[] clone = users.clone();
        clone[0].setAge(10);
        System.out.println(clone[0]);
        System.out.println(user);

    }

    static class User {
        int age;

        public User(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "age=" + age +
                    '}';
        }
    }
}
