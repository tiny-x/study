package com.xy.reflect;

import javassist.*;

import java.lang.reflect.Method;

public abstract class MethodAccess {

    public MethodAccess() {
    }

    public abstract Object invoke(Object object, int index, Object... params);

    public static MethodAccess get(Class<?> aClass) {
        try {

            ClassPool classPool = ClassPool.getDefault();
            CtClass proxyClass = classPool.makeClass("com.xy.reflect.MethodAccess_" + aClass.getSimpleName());

            CtClass superclass = classPool.get(MethodAccess.class.getName());
            proxyClass.setSuperclass(superclass);

            CtConstructor ctConstructor = new CtConstructor(null, proxyClass);
            proxyClass.addConstructor(ctConstructor);

            StringBuilder method = new StringBuilder();

            method.append("public Object invoke(Object object, int index, Object[] params) {\n");
            method.append(" switch (index) {\n");

            Method[] methods = aClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                method.append(" case " + i + ": {\n");
                method.append("    return ((" + aClass.getName() + ")object)." + methods[i].getName() + "(\n");
                Class<?>[] parameterTypes = methods[i].getParameterTypes();
                for (int j = 0; j < parameterTypes.length; j++) {
                    method.append("             (" + parameterTypes[j].getName() + ")(params[" + j + "])\n");
                }
                method.append("             );\n");
                method.append(" }\n");
            }

            method.append("     default: {\n");
            method.append("         throw new RuntimeException(\"method cant not found, index: \" + index);\n");
            method.append("     }\n");
            method.append(" }\n");
            method.append("}");

            System.out.println(method.toString());
            CtMethod ctMethod = CtNewMethod.make(method.toString(), proxyClass);
            //proxyClass.addMethod(ctMethod);
            proxyClass.writeFile("/Users/yefei/Documents/tmp/javassist");
            proxyClass.toClass().newInstance();
            return null ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object invoke1(Object object, int index, Object[] params) {
        switch (index) {
            case 0: {
                return ((com.xy.reflect.HelloService)object).hello(
                        (java.lang.String)(params[0])
                );
            }
            default: {
                throw new RuntimeException("method cant not found, index: " + index);
            }
        }
    }
}