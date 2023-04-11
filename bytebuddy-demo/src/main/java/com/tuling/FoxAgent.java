package com.tuling;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

/**
 * @author Fox
 */
public class FoxAgent {
    
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain：获取方法调用时间");
        
        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                    TypeDescription typeDescription,
                    ClassLoader classLoader) {
                return builder
                        // 拦截任意方法
                        .method(ElementMatchers.<MethodDescription>any())
                        // 指定方法拦截器，此拦截器中做具体的操作
                        .intercept(MethodDelegation.to(TimeInterceptor.class));
            }
        };
        
        AgentBuilder.Listener listener = new AgentBuilder.Listener() {
            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, DynamicType dynamicType) {}
            
            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) { }
            
            @Override
            public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) { }
            
            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) { }
        };
        
        new AgentBuilder
                .Default()
                // 指定需要拦截的类
                .type(ElementMatchers.nameStartsWith("com.tuling"))
                .transform(transformer)
                .with(listener)
                .installOn(inst);
    }
    
}
