package com.xy.reflect.method.access;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.Method;

public abstract class MethodAccess {

    private String[] methodNames;

    public static MethodAccess get(Class<?> aClass) {
        ClassPool classPool = ClassPool.getDefault();
        try {
            synchronized (aClass) {
                CtClass proxyClass = classPool.makeClass("com.xy.reflect.MethodAccess_" + aClass.getSimpleName());
                proxyClass.setSuperclass(classPool.get(MethodAccess.class.getName()));

                Method[] methods = aClass.getDeclaredMethods();
                String[] methodNames = new String[methods.length];

                StringBuilder method = new StringBuilder();
                method.append("public Object invoke(Object object, int index, Object[] params)");
                method.append("{ switch (index) {");

                for (int i = 0; i < methods.length; i++) {
                    methodNames[i] = methods[i].getName();
                    Class<?> returnType = methods[i].getReturnType();

                    method.append(" case " + i + ":");

                    if (returnType != Void.TYPE) {
                        method.append("    return");
                    }

                    method.append("    ((" + aClass.getName() + ")object)." + methods[i].getName() + "(");
                    Class<?>[] parameterTypes = methods[i].getParameterTypes();
                    for (int j = 0; j < parameterTypes.length; j++) {
                        method.append("             (" + parameterTypes[j].getName() + ")(params[" + j + "])");
                    }

                    method.append("             );");

                    if (returnType == Void.TYPE) {
                        method.append("    return null;");
                    }
                }

                method.append("     default:");
                method.append("         throw new RuntimeException(\"method cant not found, index: \" + index);");
                method.append(" }}");
                CtMethod ctMethod = CtNewMethod.make(method.toString(), proxyClass);
                proxyClass.addMethod(ctMethod);
                MethodAccess methodAccess = (MethodAccess) proxyClass.toClass().newInstance();
                methodAccess.methodNames = methodNames;
                return methodAccess;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract Object invoke(Object object, int index, Object... params);

    public Object invoke(Object object, String methodName) {
        return invoke(object, methodName, null);
    }

    public Object invoke(Object object, String methodName, Object... params) {
        for (int i = 0; i < methodNames.length; i++) {
            if (methodNames[i].equals(methodName)) {
                return invoke(object, i, params);
            }
        }
        throw new IllegalArgumentException("no method: [" + methodName + "]");
    }

}