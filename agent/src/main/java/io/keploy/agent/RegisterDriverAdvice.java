package io.keploy.agent;

import io.keploy.regression.mode;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class RegisterDriverAdvice {

    @Advice.OnMethodEnter
    public static void enterMethod(@Advice.Origin Method method, @Advice.Argument(value = 0, readOnly = false) String driverClassName) {
        System.out.println("Entering method[" + method + "] with argument[" + driverClassName + "] from EnterAdvice");
//        mode.ModeType KEPLOY_MODE = mode.getMode();
//        System.out.println(KEPLOY_MODE);
//        if (KEPLOY_MODE.equals(mode.ModeType.MODE_OFF)){
//            return;
//        }
        driverClassName = "io.keploy.ksql.KDriver";
    }

    @Advice.OnMethodExit
    public static void exitMethod(@Advice.Origin Method method) {
        System.out.println("exit advice -> " + method);
    }
}
