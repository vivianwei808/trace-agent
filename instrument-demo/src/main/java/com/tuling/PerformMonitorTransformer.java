package com.tuling;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Fox
 */
public class PerformMonitorTransformer implements ClassFileTransformer {

    private static final Set<String> classNameSet = new HashSet();
    static {
        // 直接定位
        classNameSet.add("com.tuling.HelloService");
        // 间接定位   注解  继承关系
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            String currentClassName = className.replaceAll("/", ".");
            // 只增强Set中含有的类   过滤
            if (!classNameSet.contains(currentClassName)) {
                return null;
            }
            System.out.println("transform: [" + currentClassName + "]");

            CtClass ctClass = ClassPool.getDefault().get(currentClassName);
            CtBehavior[] methods = ctClass.getDeclaredBehaviors();
            for (CtBehavior method : methods) {
                //增强方法
                enhanceMethod(method);
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void enhanceMethod(CtBehavior method) throws Exception {
        if (method.isEmpty()) {
            return;
        }
        String methodName = method.getName();
        // 不增强main方法
        if (methodName.equalsIgnoreCase("main")) {
            return;
        }
        // 增强@Fox修饰的方法
        if(null == method.getAnnotation(Fox.class)){
            return;
        }
     
        final StringBuilder source = new StringBuilder();
        source.append("{")
                    // 前置增强: 加入时间戳
                    .append("long start = System.currentTimeMillis();\n")
                    // 保留原有的代码处理逻辑
                    .append("$_ = $proceed($$);\n")
                    .append("System.out.print(\"method:[" + methodName + "]\");")
                    .append("\n")
                    // 后置增强
                    .append("System.out.println(\" cost:[\" +(System.currentTimeMillis() -start)+ \"ms]\");")
                .append("}");

        ExprEditor editor = new ExprEditor() {
          @Override
          public void edit(MethodCall methodCall) throws CannotCompileException {
              methodCall.replace(source.toString());
          }
        };
        method.instrument(editor);
    }
}