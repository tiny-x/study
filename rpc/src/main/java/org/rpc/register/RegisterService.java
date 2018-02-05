package org.rpc.register;

import org.rpc.register.bean.URL;

import java.util.List;

/**
 * The interface Register service.
 *
 * @author yefei
 * @date 2017 -06-28 10:37
 */
public interface RegisterService {

    /**
     * Register.
     *
     * @param url the url
     */
    void register(URL url);

    /**
     * Un register.
     *
     * @param url the url
     */
    void unRegister(URL url);

    /**
     * Subscribe.
     *
     * @param url            the url
     * @param notifyListener the notify listener
     */
    void subscribe(URL url, NotifyListener notifyListener);

    /**
     * Un subscribe.
     *
     * @param url the url
     */
    void unSubscribe(URL url);

    /**
     * Lookup list.
     *
     * @param url the url
     * @return the list
     */
    List<URL> lookup(URL url);
}
