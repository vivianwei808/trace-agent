package bat.ke.qq.com;

import java.lang.instrument.Instrumentation;

/**
 * @author Fox
 *
 */
public class FoxAgent {

    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("premain：" + args);

        // TODO   定位 UserService#insert   javasisst

    }
}
