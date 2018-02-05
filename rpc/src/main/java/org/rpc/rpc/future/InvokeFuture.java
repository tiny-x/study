package org.rpc.rpc.future;

/**
 * The type Delay addition future.
 *
 * @param <V> the type parameter
 * @author yefei
 * @date 2017 -7-10 15:16:53
 */
public class InvokeFuture<V> extends AbstractFuture<V> {
      
    @Override  
    public Future<V> setSuccess(Object result) {
        return super.setSuccess(result);  
    }  
      
    @Override  
    public Future<V> setFailure(Throwable cause) {
        return super.setFailure(cause);  
    }  
      
}