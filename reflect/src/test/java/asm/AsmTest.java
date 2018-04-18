package asm;

import bean.ZoomAbstract;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static org.objectweb.asm.Opcodes.*;

public class AsmTest extends ClassLoader {

    ClassWriter cw;

    @Before
    public void before() {
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_1,
                ACC_PUBLIC + ACC_SUPER,
                "com/xy/asm_proxy",
                null,
                "bean/ZoomAbstract",
                new String[]{"bean/Zoom"});

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        // 局部变量表 load this
        mv.visitVarInsn(ALOAD, 0);
        // 执行父类构造方法
        mv.visitMethodInsn(INVOKESPECIAL, "bean/ZoomAbstract", "<init>", "()V", false);
        // return
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @Test
    public void helloWorld() throws Exception {

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "ha", "()V", null, null);
        // 将System的out域入栈
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // String类型的"Hello World!"常量入栈
        mv.visitLdcInsn("Hello world asm!");
        // 调用System.out的println方法
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        // 这段代码使用了最大为2的栈元素，包含两个局部变量
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }


    @Test
    public void add() throws Exception {

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "ha", "()V", null, null);
        // 将System的out域入栈
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // int 入栈
        mv.visitIntInsn(BIPUSH, 1);
        mv.visitIntInsn(BIPUSH, 2);

        // 栈顶两int型数值相加，并且结果进栈
        mv.visitInsn(IADD);

        // 调用System.out的println方法
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    public void forAddNormal() {

    }

    /**
     * int a = 0;
     * for (int i = 0; i < 10; i++) {
     * a += i;
     * }
     * <p>
     * 0: iconst_0
     * 1: istore_1
     * 2: iconst_0
     * 3: istore_2
     * 4: iload_2
     * 5: bipush        10
     * 7: if_icmpge     20
     * 10: iload_1
     * 11: iload_2
     * 12: iadd
     * 13: istore_1
     * 14: iinc          2, 1
     * 17: goto          4
     * 20: return
     *
     * @throws Exception
     */
    @Test
    public void forAdd() throws Exception {

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "ha", "()V", null, null);

        // 常量0入栈 ，出栈存储到局部变量表 第一个位置
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 1);

        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);
        // load i
        Label label = new Label();
        Label end = new Label();

        mv.visitLabel(label);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitIntInsn(BIPUSH, 10);
        // 比较栈顶两int型数值大小，当结果大于等于0时跳转 20 指令

        mv.visitJumpInsn(IF_ICMPGE, end);

        // load a
        mv.visitVarInsn(ILOAD, 1);
        // load i
        mv.visitVarInsn(ILOAD, 2);
        // a + i
        mv.visitInsn(IADD);
        // a = a + i
        mv.visitVarInsn(ISTORE, 1);
        // i = i + 1 指定局部变量表下标2的位置 加1
        mv.visitIincInsn(2, 1);
        // goto 4 指令
        mv.visitJumpInsn(GOTO, label);


        mv.visitLabel(end);

        // 将System的out域入栈
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // load a
        mv.visitVarInsn(ILOAD, 1);

        // 调用System.out的println方法
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @After
    public void after() throws Exception {
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        File file = new File("tmp.class");
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream stream = new FileOutputStream(file);
        stream.write(bytes);
        stream.flush();

        AccessorClassLoader accessorClassLoader = AccessorClassLoader.get(ZoomAbstract.class);
        Class<?> accessorClass = accessorClassLoader.defineClass("com.xy.asm_proxy", cw.toByteArray());

        ZoomAbstract o = (ZoomAbstract) accessorClass.newInstance();
        o.ha();
    }
}
