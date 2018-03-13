package com.xy.singleton;

public class Instance {

    public Instance() {
        System.out.println("Instance init");
    }

    public static Instance getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        public static Instance instance = new Instance();
    }

    public static void main(String[] args) {
        System.out.println("main");
        Instance.getInstance();
    }
}