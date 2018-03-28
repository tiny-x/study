package com.xy.model;

import java.util.Stack;

public class StackToQueue<E> {

    private final Stack<E> in;

    private final Stack<E> out;

    public StackToQueue() {
        this.in = new Stack<>();
        this.out = new Stack<>();
    }

    public static void main(String[] args) {
        StackToQueue<String> queue = new StackToQueue<>();
        for (int i = 0; i < 10; i++) {
            queue.add(i + "");
        }

        while (!queue.isEmpty()) {
            System.out.println(queue.get());
        }
    }

    public void add(E e) {
        in.push(e);
    }

    public E get() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
        return out.pop();
    }

    public boolean isEmpty() {
        return in.isEmpty() && out.isEmpty();
    }
}
