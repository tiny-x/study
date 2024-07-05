//package com.xy.basic;
//
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.util.concurrent.TimeUnit;
//
//@Fork(1)
//@Warmup(iterations = 5)
//@Measurement(iterations = 10)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@BenchmarkMode(Mode.Throughput)
//public class ArrayCopy {
//
//    public static void main(String[] args) throws RunnerException {
//
//        Options opt = new OptionsBuilder()
//                .include(ArrayCopy.class.getSimpleName())
//                .build();
//
//        new Runner(opt).run();
//
//    }
//
//    @Benchmark
//    public void testCopy() {
//        String[] oldStrs = new String[]{
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2",
//                "1", "2", "1", "2", "1", "2", "1", "2"};
//        String[] newStrs = new String[100];
//        copy(oldStrs,
//                0,
//                newStrs,
//                0,
//                oldStrs.length
//        );
//    }
//
//    @Benchmark
//    public void testSystemCopy() {
//        String[] oldStrs = new String[]{"1", "2"};
//        String[] newStrs = new String[3];
//        System.arraycopy(oldStrs,
//                0,
//                newStrs,
//                0,
//                oldStrs.length
//        );
//
//    }
//
//    public void copy(Object src[], int srcPos,
//                     Object dest[], int destPos,
//                     int length) {
//
//        for (; srcPos < length; srcPos++, destPos++) {
//            dest[destPos] = src[srcPos];
//        }
//    }
//
//}
