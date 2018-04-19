package com.xy.jvm.gc;

import java.util.Date;

public class User {

    private final String name = "xf";

    private static int type = 0;

    private Date birthday;

    public static int getType() {
        return type;
    }

    public User(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * -XX:PermSize1m -XX:PermSize1m
     * @param args
     */
    public static void main(String[] args) {
        String getType = new StringBuilder("get").append("Type").toString();

        System.out.println(getType.intern() == getType);
    }
}
