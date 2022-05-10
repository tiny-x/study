package com.xx;

import com.xx.tranform.NumberAddTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @author yefei
 * @date 2018-05-31 15:06
 */
public class MainAgent {

    private static final Logger logger = LoggerFactory.getLogger(MainAgent.class);

    /**
     * 以Attach的方式载入，在Java程序启动后执行
     */
    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        logger.info("agentmain");

        inst.addTransformer(new NumberAddTransform(), true);
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if ("com.xx.NumberAdd".equals(allLoadedClass.getName())) {
                logger.info("retrans form Classes : {}", allLoadedClass.getName());
                inst.retransformClasses(allLoadedClass);

            }
        }
    }
}
