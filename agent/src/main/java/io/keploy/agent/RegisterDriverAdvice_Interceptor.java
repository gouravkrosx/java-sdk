package io.keploy.agent;

import io.keploy.regression.mode;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class RegisterDriverAdvice_Interceptor {

    public static String execute(@SuperCall Callable<String> client, @Origin Method method) throws Exception {
        System.out.println("Inside RegisterDriverAdvice_Interceptor -> " + method);
        String s = client.call();
        System.out.println("determineDriverClassName returns : " + s);
//        mode.ModeType KEPLOY_MODE = mode.getMode();
//        System.out.println(KEPLOY_MODE);
//        if (KEPLOY_MODE.equals(mode.ModeType.MODE_OFF)){
//            return "";
//        }
        return "io.keploy.ksql.KDriver";
    }
}
