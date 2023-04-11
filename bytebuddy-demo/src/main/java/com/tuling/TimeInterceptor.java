package com.tuling;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author Fox
 */
public class TimeInterceptor {
    
    @RuntimeType
    public static Object intercept(@Origin Method method,
            @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        try {
            // 原方法执行
            return callable.call();
        } finally {
            System.out.println(method + ": cost " + (System.currentTimeMillis() - start) + "ms");
        }
    }
    
}
