package com.xy.instrument;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author yefei
 * @date 2018-05-31 15:06
 */
public class MainAgent {

    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("agentOps: " + agentOps);
        inst.addTransformer((ClassLoader loader,
                             String className,
                             Class<?> classBeingRedefined,
                             ProtectionDomain protectionDomain,
                             byte[] classfileBuffer) -> {

            ClassPool classPool = ClassPool.getDefault();
            try {
                if ("com.xy.instrument.Main".replace(".", "/").equals(className)) {
                    className = className.replace("/", ".");
                    CtClass ctClass = classPool.get(className);
                    CtMethod ctMethod = ctClass.getDeclaredMethod("add");
                    ctMethod.insertBefore("System.out.println(\"---- intercept ----\");");
                    return ctClass.toBytecode();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void premain(String agentOps) {
        System.out.println("agentOps: " + agentOps);
    }
}
