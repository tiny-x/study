package org.rpc.register.model;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.NotifyEvent;
import org.rpc.rpc.model.ServiceMeta;

import java.util.List;

public class Notify {

    private UnresolvedAddress address;

    private NotifyEvent event;

    private ServiceMeta serviceMeta;

    private List<RegisterMeta> registerMetas;

    public Notify(UnresolvedAddress address) {
        this.address = address;
    }

    public Notify(NotifyEvent event, ServiceMeta serviceMeta, List<RegisterMeta> registerMetas) {
        this.event = event;
        this.serviceMeta = serviceMeta;
        this.registerMetas = registerMetas;
    }

    public NotifyEvent getEvent() {
        return event;
    }


    public ServiceMeta getServiceMeta() {
        return serviceMeta;
    }

    public UnresolvedAddress getAddress() {
        return address;
    }

    public List<RegisterMeta> getRegisterMetas() {
        return registerMetas;
    }

}
