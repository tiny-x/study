package benchmark;

import com.xy.reflect.Asm;
import com.xy.reflect.Cglib;
import com.xy.reflect.Direct;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@Threads(4)
public class ReflectBenchmark {

    private static final Asm asm = new Asm();

    private static final Cglib cglib = new Cglib();

    private static final Direct direct = new Direct();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReflectBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5) //预热次数
                .measurementIterations(5) //真正执行次数
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void asmInvoke() {
        asm.invoke();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void cglibInvoke() {
        try {
            cglib.invoke();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void directInvoke() {
        direct.invoke();
    }
}
