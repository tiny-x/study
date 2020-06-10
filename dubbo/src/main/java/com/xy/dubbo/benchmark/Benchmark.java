package com.xy.dubbo.benchmark;

import com.xy.dubbo.benchmark.dubbo.Consumer;
import com.xy.dubbo.benchmark.leaf.ConsumerExample;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

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

    private static final ConsumerExample consumerExample = new ConsumerExample();
    private static final Consumer consumerExample1 = new Consumer();

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void leaf() {
       consumerExample.invoke();
    }


    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void dubbo() {
        consumerExample1.invoke();
    }

}
