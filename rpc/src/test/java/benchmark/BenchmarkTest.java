package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.rpc.example.demo.ConsumerExample;

import java.util.concurrent.TimeUnit;

@Threads(4)
public class BenchmarkTest {

    private static ConsumerExample consumerExample = new ConsumerExample();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void call() {
       consumerExample.invoke();
    }

}
