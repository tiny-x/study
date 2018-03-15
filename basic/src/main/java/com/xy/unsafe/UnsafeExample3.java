package com.xy.unsafe;

import java.util.concurrent.atomic.AtomicStampedReference;

public class UnsafeExample3 {

    static volatile User user = new User(10);
    static AtomicStampedReference<User> atomicStampedReference = new AtomicStampedReference<>(user, 1);

    public static void main(String[] args) {
        atomicStampedReference.compareAndSet(user, new User(12), atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
        System.out.println(atomicStampedReference.getReference());
    }

    static class User {

        private int age;

        public User(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("User{");
            sb.append("age=").append(age);
            sb.append('}');
            return sb.toString();
        }
    }
}
