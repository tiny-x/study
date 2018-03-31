package benchmark;

import com.xy.reflect.beans.BeansCopyJavassist;
import com.xy.reflect.beans.BeansCopyAsm;
import com.xy.reflect.beans.User;
import com.xy.reflect.beans.UserVO;
import org.modelmapper.ModelMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class BeansCopyBenchmark {

    private static ModelMapper modelMapper = new ModelMapper();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BeansCopyBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void javassist() {
        User user = new User();
        user.setId(1);
        user.setName("xiaomin");
        user.setBirthDay(new Date());
        user.setInfo("aaaaaaaaa");
        UserVO userVO = new UserVO();
        BeansCopyJavassist.copyProperties(userVO, user);

    }

    @Benchmark
    public void asm() {
        User user = new User();
        user.setId(1);
        user.setName("xiaomin");
        user.setBirthDay(new Date());
        user.setInfo("aaaaaaaaa");
        UserVO userVO = new UserVO();
        BeansCopyAsm.copyProperties(userVO, user);
    }

    @Benchmark
    public void modelMapper() {
        User user = new User();
        user.setId(1);
        user.setName("xiaomin");
        user.setBirthDay(new Date());
        user.setInfo("aaaaaaaaa");
        UserVO userVO  = modelMapper.map(user ,UserVO.class);
    }
}
