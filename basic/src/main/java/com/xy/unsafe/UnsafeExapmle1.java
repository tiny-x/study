package com.xy.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeExapmle1 {

    private volatile int i = 0;

    private long ioffset;

    private volatile User user = new User(10);

    private long useroffset;

    public static Unsafe getUnsafeInstance() {
        Field theUnsafeInstance = null;
        try {
            theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            return (Unsafe) theUnsafeInstance.get(Unsafe.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UnsafeExapmle1() throws NoSuchFieldException {
        ioffset = getUnsafeInstance().objectFieldOffset(UnsafeExapmle1.class.getDeclaredField("i"));
        useroffset = getUnsafeInstance().objectFieldOffset(UnsafeExapmle1.class.getDeclaredField("user"));
    }

    public static void main(String[] args) throws NoSuchFieldException {
        UnsafeExapmle1 unsafeExapmle1 = new UnsafeExapmle1();
        Unsafe unsafe = getUnsafeInstance();
        unsafe.compareAndSwapInt(unsafeExapmle1, unsafeExapmle1.ioffset, 0, 1);
        System.out.println(unsafeExapmle1.i);

        User o2 = new User(11);
        unsafe.compareAndSwapObject(unsafeExapmle1, unsafeExapmle1.useroffset, unsafeExapmle1.user, o2);
        System.out.println(unsafeExapmle1.user);
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
