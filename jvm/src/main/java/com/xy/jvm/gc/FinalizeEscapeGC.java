package com.xy.jvm.gc;

/**
 *
 *
 */
public class FinalizeEscapeGC {

    private static FinalizeEscapeGC reference;

    public static void main(String[] args) throws InterruptedException {
        reference = new FinalizeEscapeGC();
        reference = null;
        /**
         * gc 判断reference 引用不可达,
         * 标记筛选，判断是否重写 finalize()，是否已经调用过finalize()
         * 条件成立，存放至 f-queue对象，创建低优先级线程执行finalize()
         *
         */
        System.gc();
        /**
         * finalize() 方法优先级低， 等待执行。
         */
        Thread.sleep(1000);
        if (reference != null) {
            reference.isAlive();
        }
        reference = null;
        /**
         * 再次gc 判断reference 引用不可达,
         * 标记筛选，不会在调用 finalize()
         * biao
         *
         */
        System.gc();
        if (reference != null) {
            reference.isAlive();
        } else {
            System.out.println("reference dead!");
        }
    }

    public void isAlive() {
        System.out.println("i am alive！");
    }

    /**
     * 虚拟机并不会等待 finalize() 执行完成。
     * finalize() 执行慢会导致 f-queue 其他执行finalize()对象阻塞
     * 因此用 finalize() 来自救对象本身是不靠谱得行为，应该尽量避免使用它
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        System.out.println("---------> invoke finalize()");
        reference = this;
        while (true) {
            //System.out.println("wait finalize()");
        }
    }
}
