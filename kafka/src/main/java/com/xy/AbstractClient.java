package com.xy;


import java.util.concurrent.ExecutionException;

public abstract class AbstractClient implements Runnable{

    @Override
    public void run() {
        try {
            doRun();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public abstract void doRun() throws InterruptedException, ExecutionException;
}
