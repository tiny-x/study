package org.rpc.rpc.model;

import org.rpc.remoting.api.Directory;

/**
 * 服务三元素 确定服务
 */
public class ServiceMeta extends Directory {

    private String group;

    private String serviceProviderName;

    private String version;

    public ServiceMeta(String group, String serviceProviderName, String version) {
        this.group = group;
        this.serviceProviderName = serviceProviderName;
        this.version = version;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getServiceProviderName() {
        return serviceProviderName;
    }

    @Override
    public String getVersion() {
        return version;
    }
}
