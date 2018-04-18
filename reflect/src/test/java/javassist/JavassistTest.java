package javassist;

import bean.ZoomAbstract;
import org.junit.Test;

public class JavassistTest {

    @Test
    public void main() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(this.getClass()));

        CtClass proxyClass = classPool.makeClass("com.xy.reflect.JavassistExample");
        proxyClass.setInterfaces(new CtClass[]{classPool.get("bean.Zoom")});

        proxyClass.setSuperclass(classPool.get("bean.ZoomAbstract"));

        StringBuilder stringBuilder = new StringBuilder("public void he(String[] args)");
        stringBuilder.append("{System.out.println(\"he he he ! \" + args[0]);}");
        CtMethod ctMethod = CtMethod.make(stringBuilder.toString(), proxyClass);

        CtMethod ctMethod1 = new CtMethod(CtClass.voidType, "ha", null, proxyClass);
        ctMethod1.setBody("{System.out.println(\"ha ha ha !\");}");

        proxyClass.addMethod(ctMethod);
        proxyClass.addMethod(ctMethod1);

        proxyClass.writeFile("/tmp/a/");
        ZoomAbstract o = (ZoomAbstract) proxyClass.toClass().newInstance();
        o.he(new String[]{"猪", "肥牛"});
        o.ha();
    }

}
