package com.xy.resteasy.example.netty;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.*;

import java.util.ArrayList;

/**
 * @author yefei
 * @date 2018-02-27 10:46
 */
public class Example {

    public static void main(String[] args) {
        NettyJaxrsServer netty = new NettyJaxrsServer();
        netty.setPort(8090);
        netty.setRootResourcePath("/");
        netty.setSecurityDomain(null);
        netty.setIoWorkerCount(1);

        ResteasyDeployment deployment = new ResteasyDeployment();
        netty.setDeployment(deployment);
        deployment.setRegistry(new ResourceMethodRegistry(new ResteasyProviderFactory()));
        netty.getDeployment().getRegistry().addResourceFactory(new ResourceFactoryExample());

        netty.start();
    }

    static class ResourceFactoryExample implements ResourceFactory {
        @Override
        public Class<?> getScannableClass() {
            return Library.class;
        }

        @Override
        public void registered(ResteasyProviderFactory resteasyProviderFactory) {
            System.out.println(resteasyProviderFactory);
        }

        @Override
        public Object createResource(HttpRequest httpRequest, HttpResponse httpResponse, ResteasyProviderFactory resteasyProviderFactory) {
            return new Library();
        }

        @Override
        public void requestFinished(HttpRequest httpRequest, HttpResponse httpResponse, Object o) {
            System.out.println(o);
        }

        @Override
        public void unregistered() {

        }
    }
}
