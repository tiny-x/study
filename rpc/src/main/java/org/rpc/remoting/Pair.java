package org.rpc.remoting;

public class Pair<A, B> {

    private A a;

    private B b;

    public Pair() {
    }

    public Pair(A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
