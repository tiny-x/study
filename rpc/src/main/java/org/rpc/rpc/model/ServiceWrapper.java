package org.rpc.rpc.model;

public class ServiceWrapper {

    private ServiceMeta serviceMeta;

    private Object serviceProvider;

    private int weight = 50;

    public ServiceWrapper(String group, String providerName, String version, Object serviceProvider) {
        this(group, providerName, version, serviceProvider, 0);
    }

    public ServiceWrapper(String group, String providerName, String version, Object serviceProvider, int weight) {
        this.serviceMeta = new ServiceMeta(group, providerName, version);
        this.serviceProvider = serviceProvider;
        this.weight = weight;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
