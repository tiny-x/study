package com.xy.instrument;

import com.xy.instrument.agent.ThrowException;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @author yefei
 * @date 2018-05-31 15:06
 */
public class MainAgent {

    /**
     * 以Attach的方式载入，在Java程序启动后执行
     */
    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        inst.addTransformer(new ThrowException(), true);
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            String simpleName = allLoadedClass.getName();
            if (simpleName.startsWith("com.xy")) {
                System.out.println("abc:  " + simpleName);
                inst.retransformClasses(allLoadedClass);
            }
        }
    }

}
