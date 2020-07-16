package com.xx;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;

import java.io.File;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class AsmTest {

    public static void main(String[] args) throws Exception {
        byte[] bytes = FileUtils.readFileToByteArray(new File("/Users/yefei/ali/project/study/instrument/target/classes/com/xy/instrument/NumberAdd.class"));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(cr, 0);

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "add", "(II)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ISTORE, 100);
        mv.visitVarInsn(Opcodes.ISTORE, 200);
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitInsn(Opcodes.IADD);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(6, 6);
        mv.visitEnd();

        byte[] bytes1 = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File("/tmp/a.class"), bytes1);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = classLoader.loadClass("/tmp/a.class");

        Object o = cls.newInstance();
        Method getDemoInfo = cls.getMethod("add", int.class, int.class);
        getDemoInfo.invoke(o, 1, 1);
    }

}
