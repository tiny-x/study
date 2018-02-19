package org.rpc.register.bean;

public enum DirectoryEnums {

    PROVIDERS("providers"),

    CONSUMERS("consumers");

    public String path;

    DirectoryEnums(String path) {
        this.path = path;
    }
}
