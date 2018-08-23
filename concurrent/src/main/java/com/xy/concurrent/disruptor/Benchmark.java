package com.xy.concurrent.disruptor;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.*;

@Threads(5)
public class Benchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(10) //预热次数
                .measurementIterations(10) //真正执行次数
                .build();

        new Runner(opt).run();
    }

    static DisruptorExample disruptor = new DisruptorExample();

    static Executor executor = new ThreadPoolExecutor(1, 1024, 128, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void disruptor() {
        disruptor.run(new Runnable() {
            @Override
            public void run() {
                int i = 1 , j = 0;
                i = i + j;
            }
        });
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void executor() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int i = 1 , j = 0;
                i = i + j;
            }
        });
    }
}