package com.tuling;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author Fox
 */
public class FoxAgent {
    
    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("premain：获取方法调用时间");
    
        // 添加 Transformer
        ClassFileTransformer transformer = new PerformMonitorTransformer();
        instrumentation.addTransformer(transformer);
    }
}
