package benchmark;

import com.xy.reflect.Asm;
import com.xy.reflect.Cglib;
import com.xy.reflect.Direct;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@Threads(4)
public class ReflectBenchmark {

    private static final Asm asm = new Asm();

    private static final Cglib cglib = new Cglib();

    private static final Direct direct = new Direct();

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
