package com.xy.unsafe;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class UnsafeExapmle2 {

    private static AtomicIntegerFieldUpdater<UnsafeExapmle2> atomicIntegerFieldUpdater
            = AtomicIntegerFieldUpdater.newUpdater(UnsafeExapmle2.class, "i");

    private static AtomicReferenceFieldUpdater<UnsafeExapmle2, User> atomicReferenceFieldUpdater
            = AtomicReferenceFieldUpdater.newUpdater(UnsafeExapmle2.class, User.class, "user");

    private volatile int i = 0;

    private volatile User user = new User(10);

    public static void main(String[] args) throws NoSuchFieldException {
        UnsafeExapmle2 unsafeExapmle = new UnsafeExapmle2();
        atomicIntegerFieldUpdater.compareAndSet(unsafeExapmle, 0, 2);
        System.out.println(unsafeExapmle.i);

        User o2 = new User(11);
        atomicReferenceFieldUpdater.compareAndSet(unsafeExapmle, unsafeExapmle.user, o2);
        System.out.println(o2);
    }


    static class User {

        private int age;

        public User(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
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
