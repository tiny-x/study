package org.rpc.rpc.future;

import java.util.concurrent.TimeUnit;

/**
 * The interface future.
 *
 * @param <V> the type parameter
 * @author yefei
 * @date 2017 -07-10 15:01
 */
public interface Future<V> extends java.util.concurrent.Future<V>{

    /**
     * Is success boolean.
     * 是否成功
     *
     * @return the boolean
     */
    boolean isSuccess();

    /**
     * Gets now.
     * 立即返回结果(不管Future是否处于完成状态)
     *
     * @return the now
     */
    V getNow();

    /**
     * Cause throwable.
     * 若执行失败时的原因
     *
     * @return the throwable
     */
    Throwable cause();

    /**
     * Is cancellable boolean.
     *是否可以取消
     * @return the boolean
     */
    boolean isCancellable();

    /**
     * Await future.
     * 等待future的完成
     *
     * @return the future
     * @throws InterruptedException the interrupted exception
     */
    Future<V> await() throws InterruptedException;

    /**
     * Await boolean.
     * 超时等待future的完成
     *
     * @param timeoutMillis the timeout millis
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean await(long timeoutMillis) throws InterruptedException;

    /**
     * Await boolean.
     *
     * @param timeout  the timeout
     * @param timeunit the timeunit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean await(long timeout, TimeUnit timeunit) throws InterruptedException;

    /**
     * Await uninterruptibly future.
     * 等待future的完成，不响应中断
     *
     * @return the future
     */
    Future<V> awaitUninterruptibly();

    /**
     * Await uninterruptibly boolean.
     * 超时等待future的完成，不响应中断
     *
     * @param timeoutMillis the timeout millis
     * @return the boolean
     */
    boolean awaitUninterruptibly(long timeoutMillis);

    /**
     * Await uninterruptibly boolean.
     *
     * @param timeout  the timeout
     * @param timeunit the timeunit
     * @return the boolean
     */
    boolean awaitUninterruptibly(long timeout, TimeUnit timeunit);

    /**
     * Add listener future.
     * 当future完成时，会通知这些加进来的监听器
     *
     * @param l the l
     * @return the future
     */
    Future<V> addListener(FutureListener<V> l);

    /**
     * Remove listener future.
     *
     * @param l the l
     * @return the future
     */
    Future<V> removeListener(FutureListener<V> l);
}
