package org.rpc.register;

import org.rpc.register.bean.URL;

import java.util.List;

/**
 * The interface Notify listener.
 *
 * @author yefei
 * @date 2017 -06-28 10:37
 */
public interface NotifyListener {

    /**
     * Notify.
     *
     * @param urls the urls
     */
    void notify(List<URL> urls);
}
