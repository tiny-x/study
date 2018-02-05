package org.rpc.register.bean;

/**
 * @author yefei
 * @date 2017-06-28 14:11
 */
public enum DirectoryEnums {

    PROVIDERS("providers"),

    CONSUMERS("consumers");

    public String path;

    DirectoryEnums(String path) {
        this.path = path;
    }
}
