package test.test;

public class VolatileTest {

    public static void main(String[] args) {

        for (int i = 0; i < 10000; i++) {
            final A a = new A();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    a.write();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    a.read();
                }
            }).start();
        }


    }
}

class A {

    int a;

    boolean flag;

    public void write() {
        a = 1;
        flag = true;
    }

    public void read() {
        if (flag) {
            int i = a;
            System.out.println(i);
        }
    }
}