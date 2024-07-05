import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CompileTest {

    static String className = "Testxxx";

    static String content = "import java.lang.System;\n" +
            "\n" +
            "public class Testxxx {\n" +
            "\n" +
            "    public static int add(int a, int b) throws Exception{\n" +
            "        System.out.println(\"*****\");\n" +
            "        return a + b;\n" +
            "    }\n" +
            "}\n";

    public static void main(String[] args) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 我们不使用监听器 , Locale使用Locale.getDefault()  Charset.getDefault()
        // 作用就是把File  转换wei JavaFileObject
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, Charset.defaultCharset());

        Iterable<? extends JavaFileObject> iterable = Collections.singleton(new JavaDynamicCompiler.InputStringJavaFileObject(className, content));

        // 编译参数 把结果文件生成与原文件同一个目录
        List<String> options = Arrays.asList("-d", "/tmp/");
        // 注解处理器的 类
        List<String> classes = null;
        // 创建一个编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, standardFileManager, null, options, classes, iterable);
        //JavaCompiler.CompilationTask 实现了 Callable 接口
        Boolean result = task.call();
        System.out.println(result ? "成功" : "失败");
    }

    @Test
    public void testCompile() {
        Class<?> testxxx = JavaDynamicCompiler.compileClass(Thread.currentThread().getContextClassLoader(), className, content);

    }
}
