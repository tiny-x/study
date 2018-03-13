package com.xy.singleton;

/**
 * ----------- dcl bug-----------
 * 线程1执行到 (5)-->(1),
 * 线程2执行到 (2)-->(6)-->(7),
 * 由于(1)和(7)不存在 happen before关系(线程1对i的写入不同步到主存)
 * 线程写入i=10对线程2读取i的是不可见的。
 * ---------- dcl bug-----------
 * <p>
 * -----------------------------------------------------------
 * 假设线程2进入3步骤，那就不存在这个bug，根据happen before原则分析
 * <p>
 * 线程1执行 (5) unlock happen before 线程2 (3)lock  ---> 时间上的先后unlock happen before lock
 * 线程2 (3)  happen before 线程2 (4) --> 单线程中的每个操作，happens-before于随后该线程中的任意后续操作
 * 线程1 (5)  happen before 线程2 (4) --> happen before传递性推导
 * <p>
 * 线程2 (4)  happen before 线程2 (6) --> 单线程中的每个操作，happens-before于随后该线程中的任意后续操作
 * 线程1 (5)  happen before 线程2 (6) --> happen before传递性推导
 * <p>
 * 线程2 (6)  happen before 线程2 (7) --> 单线程中的每个操作，happens-before于随后该线程中的任意后续操作
 * 线程1 (5)  happen before 线程2 (7) --> happen before传递性推导
 * <p>
 * 线程1 (1)  happen before 线程1 (5) --> 单线程中的每个操作，happens-before于随后该线程中的任意后续操作
 * <p>
 * 最终推到出 线程1 (1)  happen before 线程2 (7)
 * 因此是单例的安全的
 * -------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * 声明private static volatile Dcl dcl; 分析
 * 线程1执行到 (5)-->(1),
 * 线程2执行到 (2)-->(6)-->(7),
 * 因为dcl 是 volatile 声明， 因此
 * 线程1 (5) happen before 线程2 (7) 时间上先后对一个volatile域的写，happens-before于对这个变量的读
 * <p>
 * 最终也能推到出 线程1 (1)  happen before 线程2 (7)
 * <p>
 * ** jdk5之前对 volatile
 * 在JSR-133之前的旧Java内存模型中，虽然不允许volatile变量之间重排序，但旧的Java内
 * 存模型允许volatile变量与普通变量重排序。
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * 声明private final int i;
 * 在构造函数内对一个final域的写入，与随后把这个被构造对象的引用赋值给一个引用
 * 变量，这两个操作之间不能重排序
 * <p>
 * 线程写入i=10对线程2读取i的是可见的。
 * --------------------------------------------------------------
 * <p>
 * -------------------------------------------------------------
 * 在构造函数内对一个final引用的对象的成员域的写入，
 * 与随后在构造函数外把这个被构造对象的引用赋值给一个引用变量，这两个操作之间不能重排序。
 * -------------------------------------------------------------
 */
public class Dcl {

    private static Dcl dcl;

    private int i;

    public Dcl() {
        this.i = 10;                        // --> 1
    }

    public static Dcl instance() {
        if (dcl == null) {                  // --> 2
            synchronized (Dcl.class) {      // --> 3
                if (dcl == null) {          // --> 4
                    dcl = new Dcl();        // --> 5
                }
            }
        }
        return dcl;                         // --> 6
    }

    public int getI() {
        return i;                           // --> 7
    }
}
