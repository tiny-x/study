package benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

public class BenchmarkTest {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void str() {
        String a = "";
        for (int i = 0; i < 10; i++) {
            a += i;
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sb() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            str.append(i);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sbSync() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            str.append(i);
        }
    }
}
