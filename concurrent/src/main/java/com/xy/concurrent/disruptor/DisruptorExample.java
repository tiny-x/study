package com.xy.concurrent.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ThreadFactory;

public class DisruptorExample {

    static Disruptor<MessageEvent<Runnable>> disruptor;

    static {
        disruptor = new Disruptor<>(new MessageEventFactory(), 1024, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });

        disruptor.handleEventsWith(new MessageEventHandler());
        disruptor.start();
    }

    public static void main(String[] args) {

        RingBuffer<MessageEvent<Runnable>> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();//请求下一个事件序号；

        try {
            MessageEvent<Runnable> event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
            event.putEvent(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ha ha ha!");
                }
            });
        } finally {
            ringBuffer.publish(sequence);//发布事件；
        }
    }

    public void run(Runnable runnable) {
        RingBuffer<MessageEvent<Runnable>> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();//请求下一个事件序号；

        try {
            MessageEvent<Runnable> event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
            event.putEvent(runnable);
        } finally {
            ringBuffer.publish(sequence);//发布事件；
        }
    }


    static class MessageEvent<T> {

        private T event;

        public T getEvent() {
            return event;
        }

        public void putEvent(T event) {
            this.event = event;
        }
    }

    static class MessageEventFactory implements EventFactory<MessageEvent<Runnable>> {

        @Override
        public MessageEvent newInstance() {
            return new MessageEvent();
        }
    }

    static class MessageEventHandler implements EventHandler<MessageEvent<Runnable>> {

        @Override
        public void onEvent(MessageEvent<Runnable> event, long sequence, boolean endOfBatch) throws Exception {
            event.getEvent().run();
        }
    }
}
