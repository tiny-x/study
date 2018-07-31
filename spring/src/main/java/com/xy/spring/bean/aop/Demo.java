package com.xy.spring.bean.aop;

import com.xy.spring.Response;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;

public class Demo {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(userService);

        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AuthInterceptor()));
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new LogInterceptor()));
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new ResultConvert()));

        userService = (UserService) proxyFactory.getProxy();
        Response<?> response = userService.addUser();
        System.out.println(response);

    }

    interface UserService {
        Response<?> addUser();
    }

    static class UserServiceImpl implements UserService {

        public Response<?> addUser() {
            System.out.println("add user!");
            return Response.ofSuccess();
        }
    }

    static class LogInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            System.out.println("日志记录");
            return methodInvocation.proceed();
        }
    }

    static class AuthInterceptor implements MethodBeforeAdvice {
        @Override
        public void before(Method method, Object[] args, Object target) throws Throwable {
            System.out.println("授权");
        }
    }

    static class ResultConvert implements AfterReturningAdvice {

        @Override
        public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
            if (returnValue instanceof Response) {
                Response response = (Response) returnValue;
                response.setCode(2000);
            }
        }
    }
}
