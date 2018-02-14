package org.rpc.rpc.model;

public class ServiceWrapper {

    private ServiceMeta serviceMeta;

    private Object serviceProvider;

    public ServiceWrapper(String group, String providerName, String version, Object serviceProvider) {
        this.serviceMeta = new ServiceMeta(group, providerName, version);
        this.serviceProvider = serviceProvider;
    }

    public ServiceMeta getServiceMeta() {
        return serviceMeta;
    }

    public void setServiceMeta(ServiceMeta serviceMeta) {
        this.serviceMeta = serviceMeta;
    }

    public Object getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(Object serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}
