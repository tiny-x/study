package benchmark;

import com.xy.reflect.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class ReflectBenchmark {

    private static final ReflectAsm RELFECT_ASM = new ReflectAsm();

    private static final ReflectFastClass REFLECT_FAST_CLASS = new ReflectFastClass();

    private static final HelloService helloService = new HelloService();

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();

    private static final ReflectJava REFLECT_JAVA = new ReflectJava();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReflectBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void asmInvoke() {
        RELFECT_ASM.invoke(helloService);
    }

    @Benchmark
    public void cglibInvoke() {
        REFLECT_FAST_CLASS.invoke(helloService);
    }

    @Benchmark
    public void directInvoke() {
        helloService.hello("direct");
    }

    @Benchmark
    public void byteBuddyInvoke() {
        BYTE_BUDDY.invoke();
    }

    @Benchmark
    public void reflectJava() {
        REFLECT_JAVA.invoke(helloService);
    }
}
