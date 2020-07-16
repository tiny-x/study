package com.xx.tranform;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class NumberAddTransform implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {

        ClassPool classPool = ClassPool.getDefault();
        try {
            if ("com.xx.NumberAdd".replace(".", "/").equals(className)) {
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod ctMethod = ctClass.getDeclaredMethod("add");
                ctMethod.insertBefore("a = 100;");
                byte[] bytes = ctClass.toBytecode();
                ctClass.detach();
                FileUtils.writeByteArrayToFile(new File("/tmp/Number.class"), bytes);
                return bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}