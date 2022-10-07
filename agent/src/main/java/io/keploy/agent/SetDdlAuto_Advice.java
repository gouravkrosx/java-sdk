package io.keploy.agent;

import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

public class SetDdlAuto_Advice {

    @Advice.OnMethodEnter
    public static void enterMethod(@Advice.Origin Method method, @Advice.Argument(value = 0, readOnly = false) String ddlAuto) {
        System.out.println("Entering method[" + method + "] with argument[" + ddlAuto + "] from EnterAdvice");
//        mode.ModeType KEPLOY_MODE = mode.getMode();
//        System.out.println(KEPLOY_MODE);
//        if (KEPLOY_MODE.equals(mode.ModeType.MODE_OFF)){
//            return;
//        }
        ddlAuto = (System.getenv("KEPLOY_MODE").equals("test")) ? "none" : ddlAuto;
    }

    @Advice.OnMethodExit
    public static void exitMethod(@Advice.Origin Method method) {
        System.out.println("exit advice -> " + method);
    }
}
