package org.rpc.rpc.register;

import org.rpc.rpc.container.ServiceProviderContainer;
import org.rpc.rpc.model.ServiceWrapper;

public final class DefaultServiceRegistry implements ServiceRegistry {

    private Object serviceProvider;                     // 服务对象
    private Class<?> interfaceClass;                    // 接口类型
    private String group;                               // 服务组别
    private String providerName;                        // 服务名称
    private String version;                             // 服务版本号, 通常在接口不兼容时版本号才需要升级
    private int weight;                                 // 权重

    private ServiceProviderContainer serviceProviderContainer;

    public DefaultServiceRegistry(ServiceProviderContainer serviceProviderContainer) {
        this.serviceProviderContainer = serviceProviderContainer;
    }

    @Override
    public ServiceRegistry weight(int weight) {
        this.weight = weight;
        return this;
    }

    public Object getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(Object serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public ServiceWrapper register() {
        ServiceWrapper wrapper = new ServiceWrapper(group, providerName, version, serviceProvider);

        serviceProviderContainer.registerService(wrapper.getServiceMeta().directory(), wrapper);

        return wrapper;
    }
}