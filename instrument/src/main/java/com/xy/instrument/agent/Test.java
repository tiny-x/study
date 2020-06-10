package com.xy.instrument.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Test implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("ClassFileTransformer: " + className);
        ClassPool classPool = ClassPool.getDefault();
        try {
            if ("com.xy.instrument.Main".replace(".", "/").equals(className)) {
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod ctMethod = ctClass.getDeclaredMethod("add");
                ctMethod.insertAfter("System.out.println(\"---- intercept ----\");");
                byte[] bytes = ctClass.toBytecode();
                ctClass.detach();
                return bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}